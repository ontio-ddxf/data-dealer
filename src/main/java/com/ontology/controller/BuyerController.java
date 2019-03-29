package com.ontology.controller;

import com.ontology.controller.vo.BuyVo;
import com.ontology.controller.vo.BuyerOptVo;
import com.ontology.controller.vo.DecodeVo;
import com.ontology.controller.vo.OrderListResp;
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

import java.util.List;

@Api(tags = "数据需求方接口")
@RestController
public class BuyerController {

    @Autowired
    BuyerService buyerService;

    @ApiOperation(value="数据交易请求接口", notes="数据交易请求接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/buyer/buy", method = RequestMethod.POST)
    public Result purchaseData(@RequestBody BuyVo req) throws Exception {
        String action = "buy";
        String dataDemander = req.getDataDemander();
        String password = req.getPassword();
        String dataProvider = req.getDataProvider();
        List<String> dataIdList = req.getDataIdList();
        List<Long> priceList = req.getPriceList();

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
    public Result cancelExchange(@RequestBody BuyerOptVo req) throws Exception {
        String action = "buyerCancel";
        String dataDemander = req.getDataDemander();
        String password = req.getPassword();
        String orderId = req.getOrderId();

        helpCheckPwd(action,password);

        buyerService.cancelExchange(action,dataDemander,password,orderId);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), true);
    }

    @ApiOperation(value="需求方获取数据", notes="需求方获取数据" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/buyer/receive", method = RequestMethod.POST)
    public Result receiveEncMessage(@RequestBody BuyerOptVo req) throws Exception {
        String action = "receiveMessage";
        String dataDemander = req.getDataDemander();
        String password = req.getPassword();
        String orderId = req.getOrderId();

        helpCheckPwd(action,password);

        List<String> message = buyerService.receiveEncMessage(action,dataDemander,password,orderId);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), message);
    }

    @ApiOperation(value="需求方数据解密", notes="需求方数据解密" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/buyer/decode", method = RequestMethod.POST)
    public Result decodeMessage(@RequestBody DecodeVo req) throws Exception {
        String action = "decodeMessage";
        String dataDemander = req.getDataDemander();
        String password = req.getPassword();
        List<String> secStr = req.getMessage();

        helpCheckPwd(action,password);

        List<String> message = buyerService.decodeMessage(action,dataDemander,password,secStr);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), message);
    }



    @ApiOperation(value="需求方订单查询接口", notes="需求方订单查询接口" ,httpMethod="POST")
    @RequestMapping(value = "/api/v1/datadealer/buyer/list", method = RequestMethod.POST)
    public Result findBuyList(String buyerOntid) throws Exception {
        String action = "buyerList";

        List<OrderListResp> orderList = buyerService.findSellList(action,buyerOntid);

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), orderList);
    }

}
