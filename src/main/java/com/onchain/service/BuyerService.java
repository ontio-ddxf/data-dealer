package com.onchain.service;


import java.util.List;

public interface BuyerService {
    void purchaseData(String action, String ontid, String password, String supplyOntid, List<Integer> productIds, Double price) throws Exception;
}
