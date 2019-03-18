package com.ontology.controller;

import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.service.BuyerService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@Api("数据需求方接口")
public class BuyerController {

    @Autowired
    BuyerService buyerService;

    @ApiOperation(value="数据交易请求接口", notes="数据交易请求接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/data/dealer/buy", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result purchaseData(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "purchase";
        String ontid = (String) obj.get("ontid");
        String password = (String) obj.get("password");
        String supplyOntid = (String) obj.get("supplyOntid");
        List<Integer> productIds = (List<Integer>) obj.get("productIds");
        String price = (String) obj.get("price");

        helpCheckPwd(action,password);

        buyerService.purchaseData(action,ontid,password,supplyOntid,productIds,price);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontid);
    }

    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }
}