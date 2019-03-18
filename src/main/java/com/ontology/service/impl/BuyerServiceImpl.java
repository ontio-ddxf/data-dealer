package com.ontology.service.impl;

import com.github.ontio.account.Account;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
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
    public void purchaseData(String action, String ontid, String password, String supplyOntid, List<Integer> productIds, String price) throws Exception {
        OntId ontId = new OntId();
        ontId.setOntid(ontid);
        ontId = ontIdMapper.selectOne(ontId);
        if (ontId == null) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        if (!ontId.getPwd().equals(Helper.sha256(password))) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());

        }
        Account buyerAcct = sdk.getPayerAcct();
        Account payerAcct = sdk.getAccount(ontId.getKeystore(),password);

        List paramList = new ArrayList();

        // TODO 拼接参数
        byte[] params = BuildParams.createCodeParamsScript(paramList);

        String txHash = sdk.invokeContract(params,buyerAcct, payerAcct, 20000, 500,true);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Transaction tx = sdk.checkTx(txHash);
                    while (tx == null) {
                        sdk.invokeContract(params,buyerAcct, payerAcct, 20000, 500,true);
                        Thread.sleep(6*1000);
                        tx = sdk.checkTx(txHash);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }){}.start();

        Order order = new Order();
        order.setOntid(ontid);
        order.setSupplyOntid(supplyOntid);
        order.setTx(txHash);
        order.setState(1);
        orderMapper.insertSelective(order);
    }

}
