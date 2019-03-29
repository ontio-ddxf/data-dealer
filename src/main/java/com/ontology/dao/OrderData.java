package com.ontology.dao;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tbl_order_data")
public class OrderData {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String orderId;

    private String dataId;

}