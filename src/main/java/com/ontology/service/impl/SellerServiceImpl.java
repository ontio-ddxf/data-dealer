package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.manager.ECIES;
import com.ontology.dao.OntId;
import com.ontology.dao.Order;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OntIdMapper;
import com.ontology.mapper.OrderMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.SellerService;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SDKUtil sdk;
    @Autowired
    private OntIdMapper ontIdMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SecureConfig secureConfig;

    @Override
    public void deliverData(String action, String dataProvider, String password, String orderId, List encMsgList) throws Exception {
        OntId sellerOntId = getOntId(action,dataProvider,password);

        Account payerAcct = sdk.getPayerAcct();
        Account sellerAcct = sdk.getAccount(sellerOntId.getKeystore(),password);

        Order order = new Order();
        order.setOrderId(orderId);
        Order supplyOrder = orderMapper.selectOne(order);
        if (supplyOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        // 获取买家公钥
        OntId ontId = new OntId();
        ontId.setOntid(supplyOrder.getBuyerOntid());
        OntId buyerOntId = ontIdMapper.selectOne(ontId);
        JSONObject keyStore = JSONObject.parseObject(buyerOntId.getKeystore());
        String publicKeys = keyStore.getString("publicKey");

        // TODO 拼接参数
//        String contractHash = "65fe1aee5ab6a4bdb976c842b29bdbcdb1d2aacc";
        List argsList = new ArrayList();
        Map arg0 = new HashMap();
        arg0.put("name","exchange_id");
        log.info("exchange_id:{}",supplyOrder.getExchangeId());
        arg0.put("value","ByteArray:"+supplyOrder.getExchangeId());
        Map arg1 = new HashMap();
        arg1.put("name","message_list");
        List<String> dataIdList = new ArrayList<>();
        for (Object o : encMsgList) {
            if(o instanceof String) {
                String secStr = JSON.toJSONString(ECIES.Encrypt(publicKeys,JSON.toJSONString(o).getBytes()));
                dataIdList.add("String:" + secStr);
            }
        }
        arg1.put("value",dataIdList);//JSON.toJSONString(ECIES.Encrypt(publicKeys,JSON.toJSONString(encMsgList.get(0)).getBytes()))

        argsList.add(arg0);
        argsList.add(arg1);

        String params = Helper.getParams(dataProvider,secureConfig.getContractHash(),"sendEncMessage",argsList,payerAcct.getAddressU160().toBase58());
        log.info("params:{}",params);
        String txHash = (String) sdk.invokeContract(params,sellerAcct, payerAcct,false);

        supplyOrder.setSellTx(txHash);
        supplyOrder.setState("delivered");
        orderMapper.updateByPrimaryKey(supplyOrder);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    while (event == null) {
                        sdk.invokeContract(params,sellerAcct, payerAcct,false);
                        Thread.sleep(6*1000);
                        event = sdk.checkEvent(txHash);
                    }
                    Order orderState = orderMapper.selectOne(order);
                    orderState.setState("deliveredOnchain");
                    orderState.setSellEvent(JSON.toJSONString(event));
                    orderState.setSellDate(new Date());
                    orderMapper.updateByPrimaryKeySelective(orderState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }){}.start();
    }


    @Override
    public void confirmExchange(String action, String dataProvider, String password, String orderId) throws Exception {
        OntId buyerOntId = getOntId(action,dataProvider,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(buyerOntId.getKeystore(),password);

        Order order = new Order();
        order.setOrderId(orderId);
        Order confirmOrder = orderMapper.selectOne(order);
        if (confirmOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        log.info("confirmExchange:{}",confirmOrder.getExchangeId());
        // TODO 拼接参数
        List argsList = new ArrayList();
//        String contractHash = "65fe1aee5ab6a4bdb976c842b29bdbcdb1d2aacc";
        Map arg0 = new HashMap();
        arg0.put("name","exchange_id");
        arg0.put("value","ByteArray:"+confirmOrder.getExchangeId());
        argsList.add(arg0);

        String params = Helper.getParams(dataProvider,secureConfig.getContractHash(),"receiveToken",argsList,payerAcct.getAddressU160().toBase58());
        log.info("params:{}",params);
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        confirmOrder.setConfirmTx(txHash);
        confirmOrder.setState("sellerRecvToken");
        orderMapper.updateByPrimaryKey(confirmOrder);

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
                    orderState.setState("sellerRecvTokenOnchain");
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
    public List<Order> findSellList(String action, String sellerOntid) {
        Order order = new Order();
        order.setSellerOntid(sellerOntid);
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
