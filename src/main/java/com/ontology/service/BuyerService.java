package com.ontology.service;


import com.ontology.controller.vo.OrderListResp;
import com.ontology.dao.Order;

import java.util.List;

public interface BuyerService {
    void purchaseData(String action, String dataDemander, String password, String dataProvider, List<String> dataIdList, List<Long> priceList) throws Exception;

    void cancelExchange(String action, String dataDemander, String password, String orderId) throws Exception;

    List<String> receiveEncMessage(String action, String dataDemander, String password, String orderId) throws Exception;

//    List<String> decodeMessage(String action, String dataDemander, String password, List<String> secStr) throws Exception;
}
