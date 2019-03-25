package com.ontology.dao;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_event")
@Data
public class Event {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String ontid;

    private String tx;

    private String event;

    private Date date;

}
