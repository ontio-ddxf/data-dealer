package com.ontology.service.impl;


import com.github.ontio.account.Account;
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
        String txHash = (String) sdk.invokeContract(params,Acct, payerAcct,false);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(6*1000);
                    Object event = sdk.checkEvent(txHash);
                    while (event == null) {
                        sdk.invokeContract(params,Acct, payerAcct,false);
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
