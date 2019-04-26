package com.ontology.controller;

import com.ontology.controller.vo.*;
import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.service.IOntIdService;
import com.ontology.service.ToolsService;
import com.ontology.utils.ErrorInfo;
import com.ontology.utils.Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "工具接口")
@RestController
public class ToolsController {

    @Autowired
    private ToolsService toolsService;
    @Autowired
    private IOntIdService ontIdService;


    @ApiOperation(value="查询订单", notes="查询订单" ,httpMethod="GET")
    @RequestMapping(value = "/api/v1/data-dealer/tools/orders/{type}", method = RequestMethod.GET)
    public Result queryList(@PathVariable Integer type, String ontid) {
        String action = "queryList";

        List<OrderListResp> orderList = toolsService.queryList(action, type, ontid);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), orderList);
    }

    @ApiOperation(value="数据加密", notes="数据加密" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/data-dealer/tools/encrypt", method = RequestMethod.POST)
    public Result encrypt(@RequestBody EncryptVo req) throws Exception {
        String action = "encrypt";
        String ontid = req.getOntid();
        Integer kid = req.getKid();
        String message = req.getMessage();
        String ddo = ontIdService.getDDO(action, ontid);
        if (Helper.isEmptyOrNull(ddo)) {
            return new Result(action, ErrorInfo.NOT_EXIST.code(), ErrorInfo.NOT_EXIST.descEN(), "");
        }

        String encMsg = toolsService.encrypt(action, ddo, kid, message);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), encMsg);
    }

    @ApiOperation(value="数据解密", notes="数据解密" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/data-dealer/tools/decrypt", method = RequestMethod.POST)
    public Result decrypt(@RequestBody DecodeVo req) throws Exception {
        String action = "decrypt";
        String ontid = req.getOntid();
        String password = req.getPassword();
        Integer kid = req.getKid();
        String secStr = req.getCipher();

        helpCheckPwd(action,password);

        String ddo = ontIdService.getDDO(action, ontid);
        if (Helper.isEmptyOrNull(ddo)) {
            return new Result(action, ErrorInfo.NOT_EXIST.code(), ErrorInfo.NOT_EXIST.descEN(), "");
        }

        String decMsg = toolsService.decrypt(action,ontid,ddo,kid,password,secStr);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), decMsg);
    }


    private void helpCheckPwd(String action, String pwd) {
        if (pwd == null || pwd.length() < 8 || pwd.length() > 15) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }
    }
}
