package com.ontology.dao;

import javax.persistence.*;

@Table(name = "tbl_ontid")
public class OntId {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String ontid;

    private String phone;

    private String pwd;

    private String mail;

    private String method;

    private String keystore;

    private String tx;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return ontid
     */
    public String getOntid() {
        return ontid;
    }

    /**
     * @param ontid
     */
    public void setOntid(String ontid) {
        this.ontid = ontid == null ? null : ontid.trim();
    }

    /**
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * @return pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd
     */
    public void setPwd(String pwd) {
        this.pwd = pwd == null ? null : pwd.trim();
    }

    /**
     * @return mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail
     */
    public void setMail(String mail) {
        this.mail = mail == null ? null : mail.trim();
    }

    /**
     * @return method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    /**
     * @return keystore
     */
    public String getKeystore() {
        return keystore;
    }

    /**
     * @param keystore
     */
    public void setKeystore(String keystore) {
        this.keystore = keystore == null ? null : keystore.trim();
    }

    /**
     * @return tx
     */
    public String getTx() {
        return tx;
    }

    /**
     * @param tx
     */
    public void setTx(String tx) {
        this.tx = tx == null ? null : tx.trim();
    }
}