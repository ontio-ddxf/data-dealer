package com.onchain.controller;

import com.onchain.dao.OntId;
import com.onchain.exception.OntIdException;
import com.onchain.model.Result;
import com.onchain.service.IOntIdService;
import com.onchain.service.ISmsService;
import com.onchain.utils.ErrorInfo;
import com.onchain.utils.Helper;
import com.onchain.utils.Base64ConvertUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@RestController
public class OntIdController {

    private Logger logger = LoggerFactory.getLogger(OntIdController.class);

    @Autowired
    private IOntIdService ontIdService;


    @Autowired
    private ISmsService smsService;


    /**
     * 注册ontid
     *
     * @param method phone
     * @return ontid
     */
    @RequestMapping(value = "/api/v1/ontid/register/{method}", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result registerOntId(@PathVariable("method") String method, @RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "register";
        if (!method.equals("phone")) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        String number = (String) obj.get("number");

        //todo 手机号校验，位数校验，dto注解验证
        Helper.verifyPhone(action, number);

        String verifyCode = (String) obj.get("verifyCode");
        String password = (String) obj.get("password");
        helpCheckPwd(action, password);
        ontIdService.checkOntIdExistByPhone(action, number);

        smsService.verifyPhone(action, number, verifyCode);

        String ontidRes = ontIdService.createOntId(number, password);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontidRes);
    }

    /**
     * 登录
     *
     * @param method phone
     * @return ontid
     */
    @RequestMapping(value = "/api/v1/ontid/login/{method}", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result loginOntId(@PathVariable("method") String method, @RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "login";
        String phone = (String) obj.get("phone");
        switch (method) {
            case "phone":
                String verifyCode = (String) obj.get("verifyCode");
                return handlePhoneLogin(action, phone, verifyCode);
            case "password":
                String password = (String) obj.get("password");
                helpCheckPwd(action, password);
                return handlePasswordLogin(action, phone, password);
            default:
                throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
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
     * 修改
     *
     * @param method phone
     * @return ontid
     */
    @RequestMapping(value = "/api/v1/ontid/edit/{method}", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result updateOntId(@PathVariable("method") String method, @RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "edit";
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
