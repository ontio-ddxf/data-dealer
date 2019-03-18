package com.ontology.service;

import com.ontology.dao.OntId;

import java.security.NoSuchAlgorithmException;

public interface IOntIdService {
    String createOntId(String phone, String pwd) throws Exception;

    OntId queryOntIdByPhone(String phone);

    OntId queryOntIdByOntid(String ontid);

    void updateOntIdPassword(String action, OntId ontId, String oldPwd, String newPwd) throws Exception;

    void updateOntIdPhone(String action, OntId ontId, String newPhone, String pwd) throws NoSuchAlgorithmException;

    //如果存在异常
    void checkOntIdExistByPhone(String action, String phone);

    String exportWif(OntId ontId, String pwd) throws Exception;

    String decryptClaim(OntId ontId, String pwd, String[] data) throws Exception;

    String insertOntIdWithKeyStore(String action, String keystore, String pwd, String phone) throws Exception;
}
