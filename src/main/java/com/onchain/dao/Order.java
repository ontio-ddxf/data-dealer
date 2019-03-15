package com.onchain.dao;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tbl_order")
public class Order {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String ontid;

    private String supplyOntid;

    private String tx;

    private Integer state;

}
