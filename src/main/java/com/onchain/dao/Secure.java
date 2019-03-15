package com.onchain.dao;

import javax.persistence.*;

@Table(name = "tbl_secure")
public class Secure {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String ontid;

    private String secure;

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
     * @return secure
     */
    public String getSecure() {
        return secure;
    }

    /**
     * @param secure
     */
    public void setSecure(String secure) {
        this.secure = secure == null ? null : secure.trim();
    }
}