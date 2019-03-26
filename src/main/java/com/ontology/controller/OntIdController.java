package com.ontology.controller;

import com.ontology.controller.vo.AttributeVo;
import com.ontology.controller.vo.RegisterVo;
import com.ontology.dao.OntId;
import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.service.IOntIdService;
import com.ontology.service.ISmsService;
import com.ontology.utils.Base64ConvertUtil;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Api(tags = "Ontid 接口")
@RestController
public class OntIdController {

    private Logger logger = LoggerFactory.getLogger(OntIdController.class);

    @Autowired
    private IOntIdService ontIdService;


    @Autowired
    private ISmsService smsService;


    /**
     * 数据需求方注册ontid
     *
     * @param method phone
     * @return ontid
     */
    @ApiOperation("数据需求方注册ontid")
    @RequestMapping(value = "/api/v1/ontid/register/demander/{method}", method = RequestMethod.POST)
    public Result registerDemanderOntId(@PathVariable("method") String method, @RequestBody RegisterVo req) throws Exception {
        String action = "demanderRegister";
        Integer type = 1;
        if (!method.equals("phone")) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        String phone = req.getPhone();

        //todo 手机号校验，位数校验，dto注解验证
        Helper.verifyPhone(action, phone);

//        String verifyCode = (String) obj.get("verifyCode");
        String password = req.getPassword();
        helpCheckPwd(action, password);
        ontIdService.checkOntIdExistByPhone(action, phone);

//        smsService.verifyPhone(action, number, verifyCode);

        String ontidRes = ontIdService.createOntId(phone, password,type);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontidRes);
    }

    /**
     * 数据提供方注册ontid
     *
     * @param method phone
     * @return ontid
     */
    @ApiOperation("数据提供方注册ontid")
    @RequestMapping(value = "/api/v1/ontid/register/provider/{method}", method = RequestMethod.POST)
    public Result registerProviderOntId(@PathVariable("method") String method, @RequestBody RegisterVo req) throws Exception {
        String action = "providerRegister";
        Integer type = 2;
        if (!method.equals("phone")) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        String phone = req.getPhone();

        //todo 手机号校验，位数校验，dto注解验证
        Helper.verifyPhone(action, phone);

//        String verifyCode = (String) obj.get("verifyCode");
        String password = req.getPassword();
        helpCheckPwd(action, password);
        ontIdService.checkOntIdExistByPhone(action, phone);

//        smsService.verifyPhone(action, number, verifyCode);

        String ontidRes = ontIdService.createOntId(phone, password, type);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontidRes);
    }

    /**
     * 登录
     *
     * @param  req
     * @return ontid
     */
    @ApiOperation("Ontid 登录")
    @RequestMapping(value = "/api/v1/ontid/login", method = RequestMethod.POST)
    public Result loginOntId(@RequestBody RegisterVo req) throws Exception {
        String action = "login";
        String phone = req.getPhone();
        String password = req.getPassword();
        helpCheckPwd(action, password);
        return handlePasswordLogin(action, phone, password);
    }


    private Result handlePasswordLogin(String action, String phone, String password) {
        OntId ontId = ontIdService.queryOntIdByPhone(phone);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            if (ontId.getPwd().equals(Helper.sha256(password))) {
                return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontId.getOntid());
            } else {
                throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
            }
        }
    }

    private Result handlePhoneLogin(String action, String phone, String verifyCode) throws Exception {
        smsService.verifyPhone(action, phone, verifyCode);
        OntId ontId = ontIdService.queryOntIdByPhone(phone);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontId.getOntid());
        }
    }

    /**
     * ontid 添加属性
     *
     * @param  req
     * @return true
     */
    @ApiOperation("Ontid 添加属性")
    @RequestMapping(value = "/api/v1/ontid/addattribute", method = RequestMethod.POST)
    public Result addAttributes(@RequestBody AttributeVo req) throws Exception {
        String action = "addAttribute";
        String ontid = req.getOntid();
        String password = req.getPassword();
        String key = req.getKey();
        String valueType = req.getValueType();
        String value = req.getValue();
        OntId ontId = ontIdService.queryOntIdByOntid(ontid);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            ontIdService.addAttributes(action,ontId,password,key,valueType,value);
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
        }
    }

    /**
     * getDDO
     *
     * @param  ontid
     * @return DDO
     */
    @ApiOperation("Ontid 获取 DDO")
    @RequestMapping(value = "/api/v1/ontid/getddo", method = RequestMethod.POST)
    public Result getDDO(@RequestBody String ontid) throws Exception {
        String action = "getDDO";
        if (Helper.isEmptyOrNull(ontid)) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        String DDO = ontIdService.getDDO(action,ontid);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), DDO);
    }


    /**
     * 修改
     *
     * @param method phone
     * @return ontid
     */
    @ApiIgnore
    @RequestMapping(value = "/api/v1/ontid/update/{method}", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result updateOntId(@PathVariable("method") String method, @RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "update";
        switch (method) {
            case "phone":
                String verifyCode = (String) obj.get("verifyCode");
                return handlePhoneUpdate(action, (String) obj.get("newPhone"), verifyCode, (String) obj.get("oldPhone"), (String) obj.get("password"));
            case "password":
                return handlePasswordUpdate(action, (String) obj.get("phone"), (String) obj.get("oldPassword"), (String) obj.get("newPassword"));
            default:
                throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

    private Result handlePhoneUpdate(String action, String newPhone, String verifyCode, String oldPhone, String password) throws Exception {
        helpCheckPwd(action, password);

        // 判断新手机号是否存在
        ontIdService.checkOntIdExistByPhone(action, newPhone);

        smsService.verifyPhone(action, newPhone, verifyCode);

        OntId ontId = ontIdService.queryOntIdByPhone(oldPhone);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            ontIdService.updateOntIdPhone(action, ontId, newPhone, password);
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontId.getOntid());
        }
    }

    private Result handlePasswordUpdate(String action, String phone, String oldPassword, String newPassword) throws Exception {
        helpCheckPwd(action, oldPassword);
        helpCheckPwd(action, newPassword);


        OntId ontId = ontIdService.queryOntIdByPhone(phone);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            ontIdService.updateOntIdPassword(action, ontId, oldPassword, newPassword);
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontId.getOntid());
        }
    }

    /**
     * 导出
     *
     * @param method phone
     * @return ontid
     */
    @ApiIgnore
    @RequestMapping(value = "/api/v1/ontid/export/{method}", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result exportOntId(@PathVariable("method") String method, @RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "export";
        switch (method) {
            case "keystore":
                return handleExport(action, (String) obj.get("ontid"), (String) obj.get("password"), method);
            case "phone":
                return handleExport(action, (String) obj.get("ontid"), (String) obj.get("password"), method);
            case "wif":
                return handleExport(action, (String) obj.get("ontid"), (String) obj.get("password"), method);
            default:
                throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }


    private Result handleExport(String action, String ontid, String password, String type) throws Exception {
        helpCheckPwd(action, password);


        OntId ontId = ontIdService.queryOntIdByOntid(ontid);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            if (Helper.sha256(password).equals(ontId.getPwd())) {
                switch (type) {
                    case "keystore":
                        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontId.getKeystore());
                    case "phone":
                        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontId.getPhone());
                    case "wif":
                        String wif = ontIdService.exportWif(ontId, password);
                        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), wif);
                    default:
                        throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
                }
            } else {
                throw new OntIdException(action, ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
            }
        }
    }

    /**
     * 托管
     *
     * @return ontid
     */
    @ApiIgnore
    @RequestMapping(value = "/api/v1/ontid/binding", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result bindingOntId(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "binding";
        String phone = (String) obj.get("phone");
        String verifyCode = (String) obj.get("verifyCode");
        String keystoreBefore = (String) obj.get("keystore");
        String keystore = Base64ConvertUtil.decode(keystoreBefore);
        String password = (String) obj.get("password");

        helpCheckPwd(action, password);

        ontIdService.checkOntIdExistByPhone(action, phone);

        smsService.verifyPhone(action, phone, verifyCode);

        String ontidData = ontIdService.insertOntIdWithKeyStore(action, keystore, password, phone);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontidData);
    }

    /**
     * 校验密码
     *
     * @return ontid
     */
    @ApiIgnore
    @RequestMapping(value = "/api/v1/ontid/verify", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result verifyOntId(@RequestBody LinkedHashMap<String, Object> obj) throws NoSuchAlgorithmException {
        String action = "verify";
        return verifyOntid(action, (String) obj.get("ontid"), (String) obj.get("password"));
    }

    private Result verifyOntid(String action, String ontid, String password) throws NoSuchAlgorithmException {

        helpCheckPwd(action, password);

        OntId ontId = ontIdService.queryOntIdByOntid(ontid);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            if (Helper.sha256(password).equals(ontId.getPwd())) {
                return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
            } else {
                throw new OntIdException(action, ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
            }
        }
    }


    /**
     * 解密
     *
     * @return ontid
     */
    @ApiIgnore
    @RequestMapping(value = "/api/v1/ontid/decrypt/claim", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result decryptData(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "decrypt";
        ArrayList<String> list = ((ArrayList<String>) obj.get("message"));

        String[] encryptData = new String[list.size()];

        encryptData = list.toArray(encryptData);
        return decryptClaim(action, (String) obj.get("ontid"), (String) obj.get("password"), encryptData);
    }

    private Result decryptClaim(String action, String ontid, String password, String[] encryptData) throws Exception {
        helpCheckPwd(action, password);

        OntId ontId = ontIdService.queryOntIdByOntid(ontid);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            if (Helper.sha256(password).equals(ontId.getPwd())) {
                String rs = ontIdService.decryptClaim(ontId, password, encryptData);
                return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), rs);
            } else {
                throw new OntIdException(action, ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
            }
        }
    }

    /**
     * 查询tx
     *
     * @return ontid
     */
    @ApiIgnore
    @RequestMapping(value = "/api/v1/ontid/gettx/register/ontid", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result getTransaction(@RequestBody LinkedHashMap<String, Object> obj) throws NoSuchAlgorithmException {
        String action = "getTx";
        String ontid = (String) obj.get("ontid");
//        String password = (String) obj.get("password");
        OntId ontId = ontIdService.queryOntIdByOntid(ontid);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        } else {
            return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontId.getTx());
//            if (Helper.sha256(password).equals(ontId.getPwd())) {
//            } else {
//                throw new OntIdException(ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
//            }
        }
    }

    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }
}
