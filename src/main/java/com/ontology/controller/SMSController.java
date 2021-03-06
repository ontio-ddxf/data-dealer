package com.ontology.controller;

import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.service.impl.SmsServiceImpl;
import com.ontology.utils.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.LinkedHashMap;

@ApiIgnore
@RestController
@RequestMapping(value = "/api/v1/ontid")
public class SMSController {

    private Logger logger = LoggerFactory.getLogger(SMSController.class);

    @Autowired
    private SmsServiceImpl smsService;

    /**
     * 发送验证码
     *
     * @return
     */
    @RequestMapping(value = "/getcode/{method}", method = RequestMethod.POST, consumes = {"application/ontid.manage.api.v1+json"}, produces = {"application/ontid.manage.api.v1+json"})
    public Result getVerificationCode(@PathVariable("method") String method, @RequestBody LinkedHashMap<String, Object> obj) throws Exception {
        String action = "getVerificationCode";
        if (!method.equals("phone")) {
            throw new OntIdException(action, ErrorInfo.PARAM_ERROR.descCN(), ErrorInfo.PARAM_ERROR.descEN(), ErrorInfo.PARAM_ERROR.code());
        }

        String number = (String) obj.get("number");
        return smsService.sendCode(number);
    }

}
