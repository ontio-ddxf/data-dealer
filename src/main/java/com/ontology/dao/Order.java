package com.ontology.dao;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tbl_order")
@Data
public class Order {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String buyerOntid;

    private String buyerTx;

    private String supplyOntid;

    private String supplyTx;

    private String buyerEvent;

    private String supplyEvent;

    private Integer buyerHeight;

    private Integer supplyHeight;

    private Integer state;

}
