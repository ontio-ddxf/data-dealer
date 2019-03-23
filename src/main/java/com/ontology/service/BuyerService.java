package com.ontology.service;


import com.ontology.dao.Order;

import java.util.List;

public interface BuyerService {
    void purchaseData(String action, String ontid, String password, String supplyOntid, List<String> productIds, List<Long> price) throws Exception;

    void cancelExchange(String action, String ontid, String password, String orderId) throws Exception;

    List<Order> findSellList(String action, String buyerOntid);
}
