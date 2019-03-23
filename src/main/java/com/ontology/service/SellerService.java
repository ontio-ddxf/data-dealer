package com.ontology.service;


import com.ontology.dao.Order;

import java.util.List;

public interface SellerService {

    void deliverData(String action, String ontid, String password, String orderId, List encMsgList) throws Exception;

    void confirmExchange(String action, String ontid, String password, String orderId) throws Exception;
    List<Order> findSellList(String action, String ontid);
}
