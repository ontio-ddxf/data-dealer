package com.ontology.service;


public interface SupplyService {

    void deliverData(String action, String ontid, String password, Integer orderId, String url, String dataPwd) throws Exception;
}
