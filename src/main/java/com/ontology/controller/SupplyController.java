package com.ontology.controller;

import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.service.BuyerService;
import com.ontology.service.SupplyService;
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

@RestController
@Api("数据提供方接口")
public class SupplyController {

    @Autowired
    SupplyService supplyService;

    @ApiOperation(value="数据发货接口", notes="数据发货接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/data/dealer/supply", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result purchaseData(@RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "delivery";
        String ontid = (String) obj.get("ontid");
        String password = (String) obj.get("password");
        Integer orderId = (Integer) obj.get("orderId");
        String url = (String) obj.get("url");
        String dataPwd = (String) obj.get("dataPwd");

        helpCheckPwd(action,password);

        supplyService.deliverData(action,ontid,password,orderId,url,dataPwd);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ontid);
    }

    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }
}
