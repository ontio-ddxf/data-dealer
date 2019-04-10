package com.ontology.service;

import com.ontology.dao.OntId;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IOntIdService {
    String createOntId(String phone, String pwd, Integer type) throws Exception;

    OntId queryOntIdByPhone(String phone);

    OntId queryOntIdByOntid(String ontid);

    void updateOntIdPassword(String action, OntId ontId, String oldPwd, String newPwd) throws Exception;

    void updateOntIdPhone(String action, OntId ontId, String newPhone, String pwd) throws NoSuchAlgorithmException;

    //如果存在异常
    void checkOntIdExistByPhone(String action, String phone);

    String exportWif(OntId ontId, String pwd) throws Exception;

    String decryptClaim(OntId ontId, String pwd, String[] data) throws Exception;

    String insertOntIdWithKeyStore(String action, String keystore, String pwd, String phone) throws Exception;

    String getDDO(String action, String ontid) throws Exception;

    void addAttributes(String action, OntId ontId, String password, String key, String valueType, String value) throws Exception;

    Map<String,Object> getDdoByKey(String action, String ontid, String key) throws Exception;
}
