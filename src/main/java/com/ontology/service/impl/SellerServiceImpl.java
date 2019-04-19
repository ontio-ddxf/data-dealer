package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.manager.ECIES;
import com.ontology.controller.vo.OrderListResp;
import com.ontology.dao.OntId;
import com.ontology.dao.Order;
import com.ontology.dao.OrderData;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
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

        // 拼接参数
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
                dataIdList.add("String:" + o);
            } else {
                throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
            }
        }
        arg1.put("value",dataIdList);

        argsList.add(arg0);
        argsList.add(arg1);

        String params = Helper.getParams(dataProvider,secureConfig.getContractHash(),"sendEncMessage",argsList,payerAcct.getAddressU160().toBase58());
        log.info("params:{}",params);
        String txHash = (String) sdk.invokeContract(params,sellerAcct, payerAcct,false);

        supplyOrder.setSellTx(txHash);
        supplyOrder.setState("delivered");
        supplyOrder.setCheckTime(new Date(new Date().getTime()+10*1000));
        orderMapper.updateByPrimaryKey(supplyOrder);

//        Executors.newCachedThreadPool().submit(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(7*1000);
//                    Object event = sdk.checkEvent(txHash);
//                    int i = 0;
//                    while (Helper.isEmptyOrNull(event) && i < 5) {
//                        Thread.sleep(7*1000);
//                        event = sdk.checkEvent(txHash);
//                        i++;
//                    }
//                    Order orderState = orderMapper.selectOne(order);
//                    if (Helper.isEmptyOrNull(event)) {
//                        orderState.setState("deliveredOnchainNotFound");
//                    } else {
//                        orderState.setState("deliveredOnchain");
//                        orderState.setSellEvent(JSON.toJSONString(event));
//                    }
//                    orderState.setSellDate(new Date());
//                    orderMapper.updateByPrimaryKeySelective(orderState);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }


    @Override
    public void confirmExchange(String action, String dataProvider, String password, String orderId) throws Exception {
        OntId sellerOntId = getOntId(action,dataProvider,password);

        Account payerAcct = sdk.getPayerAcct();
        Account sellerAcct = sdk.getAccount(sellerOntId.getKeystore(),password);

        Order order = new Order();
        order.setOrderId(orderId);
        Order confirmOrder = orderMapper.selectOne(order);
        if (confirmOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        log.info("confirmExchange:{}",confirmOrder.getExchangeId());

        // 拼接参数
        List argsList = new ArrayList();
        Map arg0 = new HashMap();
        arg0.put("name","exchange_id");
        arg0.put("value","ByteArray:"+confirmOrder.getExchangeId());
        argsList.add(arg0);

        String params = Helper.getParams(dataProvider,secureConfig.getContractHash(),"receiveToken",argsList,payerAcct.getAddressU160().toBase58());
        log.info("params:{}",params);
        String txHash = (String) sdk.invokeContract(params,sellerAcct, payerAcct,false);

        confirmOrder.setRecvTokenTx(txHash);
        confirmOrder.setState("sellerRecvToken");
        confirmOrder.setCheckTime(new Date(new Date().getTime()+10*1000));
        orderMapper.updateByPrimaryKey(confirmOrder);

//        Executors.newCachedThreadPool().submit(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(7*1000);
//                    Object event = sdk.checkEvent(txHash);
//                    int i = 0;
//                    while (Helper.isEmptyOrNull(event) && i < 5) {
//                        Thread.sleep(7*1000);
//                        event = sdk.checkEvent(txHash);
//                        i++;
//                    }
//                    Order orderState = orderMapper.selectOne(order);
//                    if (Helper.isEmptyOrNull(event)) {
//                        orderState.setState("sellerRecvTokenOnchainNotFound");
//                    } else {
//                        orderState.setState("sellerRecvTokenOnchain");
//                        orderState.setRecvTokenEvent(JSON.toJSONString(event));
//                    }
//                    orderState.setRecvTokenDate(new Date());
//                    orderMapper.updateByPrimaryKeySelective(orderState);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
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
