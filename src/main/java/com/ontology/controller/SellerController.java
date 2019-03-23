package com.ontology.controller;

import com.ontology.dao.Order;
import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.service.SellerService;
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

@Api(tags = "数据提供方接口")
@RestController
public class SellerController {

    @Autowired
    SellerService sellerService;

    @ApiOperation(value="数据发货接口", notes="数据发货接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/seller/sell", method = RequestMethod.POST)
    public Result deliverData(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "deliver";
        String dataProvider = (String) obj.get("dataProvider");
        String password = (String) obj.get("password");
        String orderId = (String) obj.get("orderId");
        List encMsgList = (List)obj.get("encMessageList");


        helpCheckPwd(action,password);

        sellerService.deliverData(action,dataProvider,password,orderId,encMsgList);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }

    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

    @ApiOperation(value="需求方确认收货接口", notes="需求方确认收货接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/seller/confirm", method = RequestMethod.POST)
    public Result confirmExchange(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "confirm";
        String ontid = (String) obj.get("dataProvider");
        String password = (String) obj.get("password");
        String orderId = (String) obj.get("orderId");

        helpCheckPwd(action,password);

        sellerService.confirmExchange(action,ontid,password,orderId);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }

    @ApiOperation(value="提供方订单查询接口", notes="提供方订单查询接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/seller/list", method = RequestMethod.POST)
    public Result findSellList(String sellerOntid) throws Exception {
        String action = "sellerList";

        List<Order> orderList = sellerService.findSellList(action,sellerOntid);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), orderList);
    }
}
