package com.ontology.service.impl;


import com.alibaba.fastjson.JSON;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.ontology.controller.vo.ContractVo;
import com.ontology.dao.OntId;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.OntIdMapper;
import com.ontology.service.ContractService;
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
public class ContractServiceImpl implements ContractService {

    @Autowired
    SDKUtil sdk;
    @Autowired
    OntIdMapper ontIdMapper;
    @Override
    public void invokeContract(String action, ContractVo contractVo) throws Exception {
        String ontid = contractVo.getOntid();
        String password = contractVo.getPassword();
        String method = contractVo.getMethod();
        String contractHash = contractVo.getContractHash();
        List argsList = contractVo.getArgsList();
        OntId ontId = getOntId(action,ontid,password);
        Account payerAcct = sdk.getPayerAcct();
        Account Acct = sdk.getAccount(ontId.getKeystore(),password);
        String params = Helper.getParams(ontid, contractHash, method, argsList, payerAcct.getAddressU160().toBase58());

//        List paramList = new ArrayList();
//
//        paramList.add(Address.decodeBase58("AR9cMgFaPNDw82v1aGjmB18dfA4BvtmoeN").toArray());
//        paramList.add(Address.decodeBase58("AKRwxnCzPgBHRKczVxranWimQBFBsVkb1y").toArray());
//        paramList.add(com.github.ontio.common.Helper.hexToBytes("1ddbb682743e9d9e2b71ff419e97a9358c5c4ee9"));
//        List data = new ArrayList();
//        data.add("6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b");
//        data.add("6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b");
//        data.add("6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b");
//        paramList.add(data);
//        List priceList = new ArrayList();
//        priceList.add(1);
//        priceList.add(2);
//        priceList.add(3);
//        paramList.add(priceList);
//        paramList.add(5000);
//        paramList.add(5000);
//        byte[] params2 = BuildParams.createCodeParamsScript(paramList);
//        String txHash = sdk.invokeContract(params2, Acct, payerAcct, 20000, 500, false);

//        String txHash = (String) sdk.invokeContract(params,Acct, payerAcct,true);
        String txHash = (String) sdk.invokeContract(params,Acct, payerAcct,false);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    System.out.println(JSON.toJSONString(event));
                    while (event == null) {
//                        sdk.invokeContract(params,Acct, payerAcct,false);
//                        sdk.invokeContract(params2, Acct, payerAcct, 20000, 500, false);
                        Thread.sleep(6*1000);
                        event = sdk.checkEvent(txHash);
                    }
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
