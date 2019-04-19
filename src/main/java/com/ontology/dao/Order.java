package com.ontology.dao;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "tbl_order")
@Data
public class Order {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String orderId;

    private String exchangeId;

    private String buyerOntid;

    private String sellerOntid;

    private String buyTx;

    private String sellTx;

    private String recvTokenTx;

    private String recvMsgTx;

    private String cancelTx;

    private String buyEvent;

    private String sellEvent;

    private String recvTokenEvent;

    private String recvMsgEvent;

    private String cancelEvent;

    private Date buyDate;

    private Date sellDate;

    private Date recvTokenDate;

    private Date recvMsgDate;

    private Date cancelDate;

    private String state;

    private Date checkTime;

    @Transient
    private List<OrderData> orderData;

}
