package com.ontology.controller;

import com.ontology.controller.vo.ContractVo;
import com.ontology.model.Result;
import com.ontology.service.ContractService;
import com.ontology.service.IOntIdService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ContractController {

    @Autowired
    ContractService contractService;
    @Autowired
    IOntIdService ontIdService;

    @ApiOperation(value="合约调用接口", notes="合约调用接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/contract/invoke", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result invokeContract(@RequestBody ContractVo contractVo) throws Exception {
        String action = "invokeContract";
        contractService.invokeContract(action,contractVo);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }
}
