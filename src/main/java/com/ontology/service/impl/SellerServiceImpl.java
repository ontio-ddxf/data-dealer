package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.manager.ECIES;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.ontology.dao.OntId;
import com.ontology.dao.Order;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OntIdMapper;
import com.ontology.mapper.OrderMapper;
import com.ontology.service.SellerService;
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
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SDKUtil sdk;
    @Autowired
    private OntIdMapper ontIdMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void deliverData(String action, String ontid, String password, String orderId, String url, String dataPwd) throws Exception {
        OntId sellerOntId = getOntId(action,ontid,password);

        Account payerAcct = sdk.getPayerAcct();
        Account supplyAcct = sdk.getAccount(sellerOntId.getKeystore(),password);

        Order order = new Order();
        order.setId(orderId);
        Order supplyOrder = orderMapper.selectOne(order);
        if (supplyOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        String publicKeys = sdk.getPublicKeys(supplyOrder.getBuyerOntid());
        String securityUrl = JSON.toJSONString(ECIES.Encrypt(publicKeys,url.getBytes()));
        String securityDataPwd = JSON.toJSONString(ECIES.Encrypt(publicKeys,dataPwd.getBytes()));

        List paramList = new ArrayList();
        // TODO 拼接参数
        byte[] params = BuildParams.createCodeParamsScript(paramList);

        String txHash = sdk.invokeContract(params,supplyAcct, payerAcct, 20000, 500,true);

        supplyOrder.setSellTx(txHash);
        supplyOrder.setState("delivered");
        orderMapper.insertSelective(supplyOrder);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    while (event == null) {
                        sdk.invokeContract(params,supplyAcct, payerAcct, 20000, 500,true);
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
    public void cancelExchange(String action, String ontid, String password, String orderId) throws Exception {
        OntId sellerOntId = getOntId(action,ontid,password);

        Account payerAcct = sdk.getPayerAcct();
        Account supplyAcct = sdk.getAccount(sellerOntId.getKeystore(),password);

        Order order = new Order();
        order.setId(orderId);
        Order cancelOrder = orderMapper.selectOne(order);
        if (cancelOrder == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        List paramList = new ArrayList();
        // TODO 拼接参数
        byte[] params = BuildParams.createCodeParamsScript(paramList);

        String txHash = sdk.invokeContract(params,supplyAcct, payerAcct, 20000, 500,true);

        cancelOrder.setCancelTx(txHash);
        cancelOrder.setState("sellerCancel");
        orderMapper.insertSelective(cancelOrder);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    while (event == null) {
                        sdk.invokeContract(params,supplyAcct, payerAcct, 20000, 500,true);
                        Thread.sleep(6*1000);
                        event = sdk.checkEvent(txHash);
                    }
                    Order orderState = orderMapper.selectOne(order);
                    orderState.setState("sellerCancelOnchain");
                    orderState.setCancelEvent(JSON.toJSONString(event));
                    orderState.setCancelDate(new Date());
                    orderMapper.updateByPrimaryKeySelective(orderState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }){}.start();
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
