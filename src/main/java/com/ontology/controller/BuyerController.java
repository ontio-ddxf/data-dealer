package com.ontology.controller;

import com.ontology.dao.Order;
import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.service.BuyerService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;

@Api(tags = "数据需求方接口")
@RestController
public class BuyerController {

    @Autowired
    BuyerService buyerService;

    @ApiOperation(value="数据交易请求接口", notes="数据交易请求接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/buyer/buy", method = RequestMethod.POST)
    public Result purchaseData(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "buy";
        String dataDemander = (String) obj.get("dataDemander");
        String password = (String) obj.get("password");
        String dataProvider = (String) obj.get("dataProvider");
        List<String> dataIdList = (List<String>) obj.get("dataIdList");
        List<Long> priceList = (List<Long>) obj.get("priceList");

        helpCheckPwd(action,password);

        buyerService.purchaseData(action,dataDemander,password,dataProvider,dataIdList,priceList);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }

    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

    @ApiOperation(value="需求方交易取消接口", notes="需求方交易取消接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/buyer/cancel", method = RequestMethod.POST)
    public Result cancelExchange(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "buyerCancel";
        String ontid = (String) obj.get("dataDemander");
        String password = (String) obj.get("password");
        String orderId = (String) obj.get("orderId");

        helpCheckPwd(action,password);

        buyerService.cancelExchange(action,ontid,password,orderId);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }



    @ApiOperation(value="提供方订单查询接口", notes="提供方订单查询接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/buyer/list", method = RequestMethod.POST)
    public Result findBuyList(String buyerOntid) throws Exception {
        String action = "buyerList";

        List<Order> orderList = buyerService.findSellList(action,buyerOntid);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), orderList);
    }

}
