package com.ontology.service;


import com.ontology.controller.vo.OrderListResp;
import com.ontology.dao.Order;

import java.util.List;

public interface SellerService {

    void deliverData(String action, String dataProvider, String password, String orderId, List encMsgList) throws Exception;

    void confirmExchange(String action, String dataProvider, String password, String orderId) throws Exception;
    List<OrderListResp> findSellList(String action, String ontid);
}
