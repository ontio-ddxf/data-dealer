package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.manager.ECIES;
import com.ontology.controller.vo.OrderListResp;
import com.ontology.dao.OntId;
import com.ontology.dao.Order;
import com.ontology.dao.OrderData;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OntIdMapper;
import com.ontology.mapper.OrderDataMapper;
import com.ontology.mapper.OrderMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.service.BuyerService;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    private SDKUtil sdk;
    @Autowired
    private OntIdMapper ontIdMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SecureConfig secureConfig;
    @Autowired
    private OrderDataMapper orderDataMapper;

    @Override
    public void purchaseData(String action, String dataDemander, String password, String dataProvider, List<String> productIds, List<Long> priceList) throws Exception {
        OntId ontId = getOntId(action,dataDemander,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(ontId.getKeystore(),password);

        String dataDemanderAddr = dataDemander.replace("did:ont:","");
        String dataProviderAddr = dataProvider.replace("did:ont:","");


        // 拼接参数
        List argsList = new ArrayList();
        Map arg0 = new HashMap();
        arg0.put("name","data_demander");
        arg0.put("value","Address:"+dataDemanderAddr);
        Map arg1 = new HashMap();
        arg1.put("name","data_provider");
        arg1.put("value","Address:"+dataProviderAddr);
        Map arg2 = new HashMap();
        arg2.put("name","token_contract_address");
        arg2.put("value","ByteArray:0000000000000000000000000000000000000002");//1ddbb682743e9d9e2b71ff419e97a9358c5c4ee9
        Map arg3 = new HashMap();
        arg3.put("name","data_id_list");
        List<String> dataIdList = new ArrayList<>();
        for (String s : productIds) {
            dataIdList.add("String:"+s);
        }
        arg3.put("value",dataIdList);
        Map arg4 = new HashMap();
        arg4.put("name","price_list");
        arg4.put("value",priceList);
        Map arg5 = new HashMap();
        arg5.put("name","wait_send_enc_list_time");
        arg5.put("value",60000);
        argsList.add(arg0);
        argsList.add(arg1);
        argsList.add(arg2);
        argsList.add(arg3);
        argsList.add(arg4);
        argsList.add(arg5);
        String params = Helper.getParams(dataDemander,secureConfig.getContractHash() ,"sendToken",argsList,payerAcct.getAddressU160().toBase58());
        log.info("invoke params:{}",params);
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setBuyerOntid(dataDemander);
        order.setSellerOntid(dataProvider);
        order.setBuyTx(txHash);
        order.setState("bought");
        order.setCheckTime(new Date(new Date().getTime()+10*1000));
        orderMapper.insertSelective(order);

        List<OrderData> ods = new ArrayList<>();
        for (String dataId : productIds) {
            OrderData od = new OrderData();
            od.setId(UUID.randomUUID().toString());
            od.setOrderId(order.getOrderId());
            od.setDataId(dataId);
            ods.add(od);
        }
        orderDataMapper.insertList(ods);

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
//                        orderState.setState("boughtOnchainNotFound");
//                    } else {
//                        String eventStr = JSON.toJSONString(event);
//                        String exchangeId = null;
//                        JSONObject jsonObject = JSONObject.parseObject(eventStr);
//                        JSONArray notify = jsonObject.getJSONArray("Notify");
//                        for (int j = 0;j<notify.size();j++) {
//                            JSONObject obj = notify.getJSONObject(j);
//                            if (secureConfig.getContractHash().equals(obj.getString("ContractAddress"))) {
//                                exchangeId = obj.getJSONArray("States").getString(1);
//                            }
//                        }
//                        orderState.setBuyEvent(eventStr);
//                        orderState.setExchangeId(exchangeId);
//                        orderState.setState("boughtOnchain");
//                    }
//                    orderState.setBuyDate(new Date());
//                    orderMapper.updateByPrimaryKeySelective(orderState);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }



    @Override
    public void cancelExchange(String action, String dataDemander, String password, String orderId) throws Exception {
        OntId buyerOntId = getOntId(action,dataDemander,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(buyerOntId.getKeystore(),password);

        Order order = new Order();
        order.setOrderId(orderId);
        Order cancelOrder = orderMapper.selectOne(order);
        if (cancelOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        // TODO 拼接参数
        List argsList = new ArrayList();
        Map arg0 = new HashMap();
        arg0.put("name","exchange_id");
        arg0.put("value","ByteArray:"+cancelOrder.getExchangeId());
        argsList.add(arg0);

        String params =  Helper.getParams(dataDemander,secureConfig.getContractHash(),"cancelExchange",argsList,payerAcct.getAddressU160().toBase58());
        log.info("cancelExchange:{}",params);
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        cancelOrder.setCancelTx(txHash);
        cancelOrder.setState("buyerCancel");
        cancelOrder.setCheckTime(new Date(new Date().getTime()+10*1000));
        orderMapper.updateByPrimaryKey(cancelOrder);

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
//                        orderState.setState("buyerCancelOnchainNotFound");
//                    } else {
//                        orderState.setState("buyerCancelOnchain");
//                        orderState.setCancelEvent(JSON.toJSONString(event));
//                    }
//                    orderState.setCancelDate(new Date());
//                    orderMapper.updateByPrimaryKeySelective(orderState);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public List<String> receiveEncMessage(String action, String dataDemander, String password, String orderId) throws Exception {
        OntId buyerOntId = getOntId(action,dataDemander,password);

        Account payerAcct = sdk.getPayerAcct();
        Account buyerAcct = sdk.getAccount(buyerOntId.getKeystore(),password);

        Order order = new Order();
        order.setOrderId(orderId);
        Order receiveOrder = orderMapper.selectOne(order);
        if (receiveOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        if (Helper.isEmptyOrNull(receiveOrder.getSellEvent())) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        // 拼接参数
        List argsList = new ArrayList();
        Map arg0 = new HashMap();
        arg0.put("name","exchange_id");
        arg0.put("value","ByteArray:"+receiveOrder.getExchangeId());
        argsList.add(arg0);

        String params =  Helper.getParams(dataDemander,secureConfig.getContractHash(),"receiveEncMessage",argsList,payerAcct.getAddressU160().toBase58());
        log.info("receiveEncMessage:{}",params);
        String txHash = (String) sdk.invokeContract(params,buyerAcct, payerAcct,false);

        String sellEvent = receiveOrder.getSellEvent();
        JSONObject jsonObject = JSONObject.parseObject(sellEvent);
        JSONArray notify = jsonObject.getJSONArray("Notify");
        String data = "";
        for (int i = 0;i<notify.size();i++) {
            JSONObject obj = notify.getJSONObject(i);
            if (secureConfig.getContractHash().equals(obj.getString("ContractAddress"))) {
                data = obj.getString("States");
                data = new String(com.github.ontio.common.Helper.hexToBytes(data),"utf-8");
                break;
            }
        }
        if (Helper.isEmptyOrNull(data)) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        String[] dataArray = data.split("#");
        List<String> dataList = Arrays.asList(dataArray);

        receiveOrder.setRecvMsgTx(txHash);
        receiveOrder.setState("buyerRecvMsg");
        receiveOrder.setCheckTime(new Date(new Date().getTime()+10*1000));
        orderMapper.updateByPrimaryKey(receiveOrder);

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
//                        orderState.setState("buyerRecvMsgOnchainNotFound");
//                    } else {
//                        orderState.setRecvMsgEvent(JSON.toJSONString(event));
//                        orderState.setState("buyerRecvMsgOnchain");
//                    }
//                    orderState.setRecvMsgDate(new Date());
//                    orderMapper.updateByPrimaryKeySelective(orderState);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        return dataList;

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
