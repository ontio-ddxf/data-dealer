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

import java.util.*;

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
    public void purchaseData(String action, String ontid, String password, String sellerOntid, List<String> productIds, List<String> priceList) throws Exception {
        OntId ontId = getOntId(action,ontid,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(ontId.getKeystore(),password);

        byte[] buyerAddr = ontid.replace("did:ont:","").getBytes();
        byte[] sellerAddr = sellerOntid.replace("did:ont:","").getBytes();

//        byte[] buyerAddr = Address.addressFromPubKey(sdk.getPublicKeys(ontid)).toBase58().getBytes();
//        byte[] supplyAddr = Address.addressFromPubKey(sdk.getPublicKeys(sellerOntid)).toBase58().getBytes();


        // TODO 拼接参数
        String contractHash = "7a929de3dcf464d92e79f4ad04c41f56245998e5";
        List argsList = new ArrayList();
        Map arg0 = new HashMap();
        arg0.put("name","data_demander");
        arg0.put("value","bytearray:"+Arrays.toString(buyerAddr));
        Map arg1 = new HashMap();
        arg1.put("name","data_provider");
        arg1.put("value","bytearray:"+Arrays.toString(sellerAddr));
        Map arg2 = new HashMap();
        arg2.put("name","token_contract_address");
        arg2.put("value","bytearray:"+Arrays.toString("d7b6a47966770c1545bf74c16426b26c0a238b16".getBytes()));
        Map arg3 = new HashMap();
        arg3.put("name","data_id_list");
        arg3.put("value","list:"+JSON.toJSONString(productIds));
        Map arg4 = new HashMap();
        arg4.put("name","price_list");
        arg4.put("value","list:"+JSON.toJSONString(priceList));
        Map arg5 = new HashMap();
        arg5.put("name","wait_send_enc_list_time");
        arg5.put("value","int:5000");
        Map arg6 = new HashMap();
        arg6.put("name","wait_receive_enc_list_time");
        arg6.put("value","int:5000");
        argsList.add(arg0);
        argsList.add(arg1);
        argsList.add(arg2);
        argsList.add(arg3);
        argsList.add(arg4);
        argsList.add(arg5);
        argsList.add(arg6);
        String params = Helper.getParams(ontid,contractHash,"send_token",argsList,payerAcct.getAddressU160().toBase58());
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        Order order = new Order();
        order.setId(""+System.currentTimeMillis());
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
