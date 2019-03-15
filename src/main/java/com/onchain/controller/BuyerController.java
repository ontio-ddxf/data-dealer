package com.onchain.controller;

import com.github.ontio.sdk.wallet.Identity;
import com.onchain.exception.OntIdException;
import com.onchain.model.Result;
import com.onchain.service.BuyerService;
import com.onchain.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name="obj", value="请求信息", dataType = "Map", required=true, paramType="form"),
    })
    @RequestMapping(value = "/api/v1/data/dealer/buy", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result purchaseData(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "purchase";
        String ontid = (String) obj.get("ontid");
        String password = (String) obj.get("password");
        String supplyOntid = (String) obj.get("supplyOntid");
        List<Integer> productIds = (List<Integer>) obj.get("productIds");
        Double price = (Double) obj.get("price");

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
