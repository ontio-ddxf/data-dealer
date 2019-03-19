package com.ontology.service;


public interface SellerService {

    void deliverData(String action, String ontid, String password, String orderId, String url, String dataPwd) throws Exception;

    void cancelExchange(String action, String ontid, String password, String orderId) throws Exception;
}
