package com.ontology.service;


import java.util.List;

public interface BuyerService {
    void purchaseData(String action, String ontid, String password, String supplyOntid, List<Integer> productIds, String price) throws Exception;

    void cancelExchange(String action, String ontid, String password, String orderId) throws Exception;

    void confirmExchange(String action, String ontid, String password, String orderId) throws Exception;
}
