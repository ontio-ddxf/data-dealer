package com.ontology.controller;

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

@RestController
@Api("数据提供方接口")
public class SellerController {

    @Autowired
    SellerService sellerService;

    @ApiOperation(value="数据发货接口", notes="数据发货接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/seller/sell", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result purchaseData(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "deliver";
        String ontid = (String) obj.get("ontid");
        String password = (String) obj.get("password");
        String orderId = (String) obj.get("orderId");
        String url = (String) obj.get("url");
        String dataPwd = (String) obj.get("dataPwd");

        helpCheckPwd(action,password);

        sellerService.deliverData(action,ontid,password,orderId,url,dataPwd);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontid);
    }

    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }

    @ApiOperation(value="提供方交易取消接口", notes="提供方交易取消接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/seller/cancel", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result cancelExchange(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "sellerCancel";
        String ontid = (String) obj.get("ontid");
        String password = (String) obj.get("password");
        String orderId = (String) obj.get("orderId");

        helpCheckPwd(action,password);

        sellerService.cancelExchange(action,ontid,password,orderId);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontid);
    }
}
