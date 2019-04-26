package com.ontology.controller;

import com.ontology.controller.vo.ContractVo;
import com.ontology.model.Result;
import com.ontology.service.ContractService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "合約调用接口")
@RestController
public class ContractController {

    @Autowired
    private ContractService contractService;


    @ApiOperation(value="合约调用接口", notes="合约调用接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/data-dealer/contract", method = RequestMethod.POST)
    public Result invokeContract(@RequestBody ContractVo contractVo) throws Exception {
        String action = "invokeContract";
        contractService.invokeContract(action,contractVo);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }
}
