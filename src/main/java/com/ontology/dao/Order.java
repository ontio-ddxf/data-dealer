package com.ontology.dao;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_order")
@Data
public class Order {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String exchangeId;

    private String buyerOntid;

    private String sellerOntid;

    private String buyTx;

    private String sellTx;

    private String cancelTx;

    private String confirmTx;

    private String buyEvent;

    private String sellEvent;

    private String cancelEvent;

    private String confirmEvent;

    private Date buyDate;

    private Date sellDate;

    private Date cancelDate;

    private Date confirmDate;

    private String state;

}
