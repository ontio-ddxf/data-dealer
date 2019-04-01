package com.ontology.service.impl;

import com.ontology.controller.vo.OrderListResp;

import java.util.List;

public interface ToolsService {

    List<OrderListResp> queryList(String action, Integer provider, String ontid);

    String encrypt(String action, String ddo, Integer kid, String message);

    String decrypt(String action, String ontid, String ddo, Integer kid, String password, String secStr) throws Exception;
}
