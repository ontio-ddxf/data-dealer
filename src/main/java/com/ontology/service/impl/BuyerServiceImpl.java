package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.ontology.dao.OntId;
import com.ontology.dao.Order;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OntIdMapper;
import com.ontology.mapper.OrderMapper;
import com.ontology.service.BuyerService;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    private SDKUtil sdk;
    @Autowired
    private OntIdMapper ontIdMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void purchaseData(String action, String ontid, String password, String sellerOntid, List<Integer> productIds, String price) throws Exception {
        OntId ontId = getOntId(action,ontid,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(ontId.getKeystore(),password);

        byte[] buyerAddr = Address.addressFromPubKey(sdk.getPublicKeys(ontid)).toBase58().getBytes();
        byte[] supplyAddr = Address.addressFromPubKey(sdk.getPublicKeys(sellerOntid)).toBase58().getBytes();

        // TODO 拼接参数
        List argsList = new ArrayList();

        String params = Helper.getParams(ontid,"",null,argsList,payerAcct.getAddressU160().toBase58());
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        Order order = new Order();
        order.setBuyerOntid(ontid);
        order.setSellerOntid(sellerOntid);
        order.setBuyTx(txHash);
        order.setState("bought");
        orderMapper.insertSelective(order);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    while (event == null) {
                        sdk.invokeContract(params,buyerAcct, payerAcct,false);
                        Thread.sleep(6*1000);
                        event = sdk.checkEvent(txHash);
                    }
                    Order orderState = orderMapper.selectOne(order);
                    orderState.setState("boughtOnchain");
                    orderState.setBuyEvent(JSON.toJSONString(event));
                    orderState.setBuyDate(new Date());
                    orderMapper.updateByPrimaryKeySelective(orderState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }){}.start();

    }



    @Override
    public void cancelExchange(String action, String ontid, String password, String orderId) throws Exception {
        OntId buyerOntId = getOntId(action,ontid,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(buyerOntId.getKeystore(),password);

        Order order = new Order();
        order.setId(orderId);
        Order cancelOrder = orderMapper.selectOne(order);
        if (cancelOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        // TODO 拼接参数
        List argsList = new ArrayList();

        String params =  Helper.getParams(ontid,"",null,argsList,payerAcct.getAddressU160().toBase58());
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        cancelOrder.setCancelTx(txHash);
        cancelOrder.setState("buyerCancel");
        orderMapper.insertSelective(cancelOrder);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    while (event == null) {
                        sdk.invokeContract(params,buyerAcct, payerAcct,false);
                        Thread.sleep(6*1000);
                        event = sdk.checkEvent(txHash);
                    }
                    Order orderState = orderMapper.selectOne(order);
                    orderState.setState("buyerCancelOnchain");
                    orderState.setCancelEvent(JSON.toJSONString(event));
                    orderState.setCancelDate(new Date());
                    orderMapper.updateByPrimaryKeySelective(orderState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }){}.start();
    }

    @Override
    public void confirmExchange(String action, String ontid, String password, String orderId) throws Exception {
        OntId buyerOntId = getOntId(action,ontid,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(buyerOntId.getKeystore(),password);

        Order order = new Order();
        order.setId(orderId);
        Order confirmOrder = orderMapper.selectOne(order);
        if (confirmOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        // TODO 拼接参数
        List argsList = new ArrayList();

        String params = Helper.getParams(ontid,"",null,argsList,payerAcct.getAddressU160().toBase58());
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        confirmOrder.setConfirmTx(txHash);
        confirmOrder.setState("buyerConfirm");
        orderMapper.insertSelective(confirmOrder);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    while (event == null) {
                        sdk.invokeContract(params,buyerAcct, payerAcct,false);
                        Thread.sleep(6*1000);
                        event = sdk.checkEvent(txHash);
                    }
                    Order orderState = orderMapper.selectOne(order);
                    orderState.setState("buyerConfirmOnchain");
                    orderState.setConfirmEvent(JSON.toJSONString(event));
                    orderState.setConfirmDate(new Date());
                    orderMapper.updateByPrimaryKeySelective(orderState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }){}.start();
    }

    @Override
    public List<Order> findSellList(String action, String buyerOntid) {
        Order order = new Order();
        order.setSellerOntid(buyerOntid);
        List<Order> orderList = orderMapper.select(order);
        return orderList;
    }

    private OntId getOntId(String action, String ontid, String password) {
        OntId ontId = new OntId();
        ontId.setOntid(ontid);
        ontId = ontIdMapper.selectOne(ontId);

        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        if (!ontId.getPwd().equals(Helper.sha256(password))) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        return ontId;
    }


}
