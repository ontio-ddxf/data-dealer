package com.ontology.controller;

import com.ontology.controller.vo.ConfirmVo;
import com.ontology.controller.vo.OrderListResp;
import com.ontology.controller.vo.SellVo;
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
    private SellerService sellerService;

    @ApiOperation(value="数据发货接口", notes="数据发货接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/data-dealer/seller", method = RequestMethod.POST)
    public Result deliverData(@RequestBody SellVo req) throws Exception {
        String action = "deliver";
        String dataProvider = req.getDataProvider();
        String password = req.getPassword();
        String orderId = req.getOrderId();
        List encMsgList = req.getEncMessageList();


        helpCheckPwd(action,password);

        sellerService.deliverData(action,dataProvider,password,orderId,encMsgList);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }

    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

    @ApiOperation(value="提供方收取Token接口", notes="提供方收取Token接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/data-dealer/seller/token", method = RequestMethod.POST)
    public Result confirmExchange(@RequestBody ConfirmVo req) throws Exception {
        String action = "getToken";
        String dataProvider = req.getDataProvider();
        String password = req.getPassword();
        String orderId = req.getOrderId();

        helpCheckPwd(action,password);

        sellerService.confirmExchange(action,dataProvider,password,orderId);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }
}
