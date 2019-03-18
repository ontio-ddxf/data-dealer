package com.ontology.service.impl;

import com.ontology.ConfigParam;
import com.ontology.dao.OntId;
import com.ontology.dao.Secure;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OntIdMapper;
import com.ontology.mapper.SecureMapper;
import com.ontology.secure.RSAUtil;
import com.ontology.service.IOntIdService;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by ZhouQ on 2017/8/31.
 */
@Slf4j
@Service("OntIdServiceImpl")
public class OntIdServiceImpl implements IOntIdService {

    @Autowired
    RSAUtil utilRSA;

    @Autowired
    private OntIdMapper mapper;

    @Autowired
    private SecureMapper secureMapper;

    @Autowired
    private SDKUtil sdk;

    @Autowired
    private ConfigParam param;


    @Override
    public String createOntId(String phone, String pwd) throws Exception {
        Map<String, String> createRes = sdk.createOntId(pwd);
        String ontid = createRes.get("ontid");
        String keystore = createRes.get("keystore");
        String tx = createRes.get("tx");
        OntId ontidBean = new OntId();
        ontidBean.setOntid(ontid);
        ontidBean.setPhone(phone);
        ontidBean.setKeystore(keystore);
        ontidBean.setMethod("phone");
        ontidBean.setPwd(Helper.sha256(pwd));
        ontidBean.setTx(tx);
        mapper.insertSelective(ontidBean);

        insertOrUpdateSecrue(ontid, pwd);

        return ontid;
    }

    @Override
    public OntId queryOntIdByPhone(String phone) {
        OntId ontId = new OntId();
        ontId.setPhone(phone);
        return mapper.selectOne(ontId);
    }

    @Override
    public void updateOntIdPassword(String action, OntId ontId, String oldPwd, String newPwd) throws Exception {
        if (!Helper.sha256(oldPwd).equals(ontId.getPwd())) {
            throw new OntIdException(action, ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
        }
        String wif = sdk.exportWif(ontId.getKeystore(), oldPwd);
        String keystore = sdk.createOntIdWithWif(wif, newPwd);
        ontId.setKeystore(keystore);
        ontId.setPwd(Helper.sha256(newPwd));
        mapper.updateByPrimaryKeySelective(ontId);
        insertOrUpdateSecrue(ontId.getOntid(), newPwd);
    }

    @Override
    public void updateOntIdPhone(String action, OntId ontId, String newPhone, String pwd) {
        if (Helper.sha256(pwd).equals(ontId.getPwd())) {
            ontId.setPhone(newPhone);
            mapper.updateByPrimaryKeySelective(ontId);
        } else {
            throw new OntIdException(action, ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
        }
    }

    @Override
    public void checkOntIdExistByPhone(String action, String phone) {
        OntId ontId = queryOntIdByPhone(phone);
        if (ontId != null) {
            throw new OntIdException(action, ErrorInfo.ALREADY_EXIST.descCN(), ErrorInfo.ALREADY_EXIST.descEN(), ErrorInfo.ALREADY_EXIST.code());
        }
    }

    @Override
    public String exportWif(OntId ontId, String pwd) throws Exception {
        return sdk.exportWif(ontId.getKeystore(), pwd);
    }

    @Override
    public String decryptClaim(OntId ontId, String pwd, String[] data) throws Exception {
        return sdk.decryptData(ontId.getKeystore(), pwd, data);
    }

    @Override
    public String insertOntIdWithKeyStore(String action, String keystore, String pwd, String phone) throws Exception {
        String ontidStr = sdk.checkOntId(keystore, pwd);

        //校验keystore的ONT ID是否注册过，如果未注册过，返回失败，该未注册
        String ontidDDO = sdk.checkOntIdDDO(ontidStr);
        if (Helper.isEmptyOrNull(ontidDDO)) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        //ontId是否存在
        OntId checkOntid = queryOntIdByOntid(ontidStr);
        if (checkOntid != null) {
            throw new OntIdException(action, ErrorInfo.ALREADY_EXIST.descCN(), ErrorInfo.ALREADY_EXIST.descEN(), ErrorInfo.ALREADY_EXIST.code());
        } else {
            checkOntid = new OntId();
            checkOntid.setPwd(Helper.sha256(pwd));
            checkOntid.setKeystore(keystore);
            checkOntid.setOntid(ontidStr);
            checkOntid.setPhone(phone);
            checkOntid.setTx("");
            checkOntid.setMethod("phone");
            mapper.insertSelective(checkOntid);

            insertOrUpdateSecrue(ontidStr, pwd);
            return ontidStr;
        }
    }


    @Override
    public OntId queryOntIdByOntid(String ontid) {
        OntId ontId = new OntId();
        ontId.setOntid(ontid);
        return mapper.selectOne(ontId);
    }

    private void insertOrUpdateSecrue(String ontid, String password) throws Exception {
        //RSA 公钥加密
        String secureData = utilRSA.encryptByPublicKey(password);
        Secure secure = new Secure();
        secure.setOntid(ontid);
        secure = secureMapper.selectOne(secure);
        if (secure == null) {
            secure = new Secure();
            secure.setOntid(ontid);
            secure.setSecure(secureData);
            secureMapper.insertSelective(secure);
        } else {
            secure.setSecure(secureData);
            secureMapper.updateByPrimaryKeySelective(secure);
        }
    }
}
