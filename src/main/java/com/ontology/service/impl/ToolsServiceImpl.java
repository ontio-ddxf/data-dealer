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
import com.ontology.model.Result;
import com.ontology.secure.SecureConfig;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ToolsServiceImpl implements ToolsService {
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
    public List<OrderListResp> queryList(String action, Integer provider, String buyerOntid) {
        String queryType = null;
        if (0==provider) {
            // 需求方
            queryType = "buyer_ontid";
        } else if (1==provider) {
            // 提供方
            queryType = "seller_ontid";
        } else {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
        List<Order> orderList = orderMapper.getOrderList(queryType,buyerOntid);
        List<OrderListResp> resps = new ArrayList<>();
        for (Order order:orderList) {
            OrderListResp resp = new OrderListResp();
            resp.setOrderId(order.getOrderId());
            resp.setBuyDate(order.getBuyDate());
            if (0==provider) {
                resp.setDataProvider(order.getSellerOntid());
            } else {
                resp.setDataDemander(order.getBuyerOntid());
            }
            resp.setState(order.getState());
            List<String> dataList = new ArrayList<>();
            for (OrderData data:order.getOrderData()){
                String dataId = data.getDataId();
                dataList.add(dataId);
            }
            resp.setDataIdList(dataList);
            resps.add(resp);
        }
        return resps;
    }

    @Override
    public String encrypt(String action, String ddo, Integer kid, String message) {
        String publicKey = getPublicKey(action,ddo,kid);

        String secStr = JSON.toJSONString(ECIES.Encrypt(publicKey,JSON.toJSONString(message).getBytes()));
        return secStr;
    }

    private String getPublicKey(String action, String ddo, Integer kid) {
        String publicKey = null;
        JSONObject jsonObject = JSONObject.parseObject(ddo);
        JSONArray owners = jsonObject.getJSONArray("Owners");
        for (int i=0;i<owners.size();i++) {
            JSONObject obj = owners.getJSONObject(i);
            if (obj.getString("PubKeyId").endsWith("keys-"+kid)) {
                publicKey = obj.getString("Value");
                break;
            }
        }
        if (Helper.isEmptyOrNull(publicKey)) {
            throw new OntIdException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }
        return publicKey;
    }

    @Override
    public String decrypt(String action, String ontid, String ddo, Integer kid, String password, String secStr) throws Exception {
        String publicKey = getPublicKey(action,ddo,kid);

        OntId OntId = getOntId(action, ontid, password);
        JSONObject jsonObject = JSONObject.parseObject(OntId.getKeystore());
        if (publicKey.equals(jsonObject.getString("publicKey"))) {
            Account buyerAcct = sdk.getAccount(OntId.getKeystore(), password);
            Object[] objects = JSONArray.parseArray(secStr).toArray();
            String[] msg = new String[objects.length];
            for (int i = 0; i < objects.length; i++) {
                msg[i] = (String) objects[i];
            }
            byte[] decrypt = ECIES.Decrypt(buyerAcct, msg);
            String message = new String(decrypt, "utf-8").replace("\"", "");
            log.info("{}", message);
            return message;
        } else {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
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
