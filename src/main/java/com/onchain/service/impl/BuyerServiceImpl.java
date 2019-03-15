package com.onchain.service.impl;

import com.github.ontio.account.Account;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.onchain.dao.OntId;
import com.onchain.dao.Order;
import com.onchain.exception.OntIdException;
import com.onchain.mapper.OntIdMapper;
import com.onchain.mapper.OrderMapper;
import com.onchain.service.BuyerService;
import com.onchain.utils.ErrorInfo;
import com.onchain.utils.Helper;
import com.onchain.utils.SDKUtil;
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
    public void purchaseData(String action, String ontid, String password, String supplyOntid, List<Integer> productIds, Double price) throws Exception {
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
        byte[] params = BuildParams.createCodeParamsScript(paramList);

        String txHash = sdk.invokeContract(params,buyerAcct, payerAcct, 20000, 500,true);

        Order order = new Order();
    }

}
