package com.ontology.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContractVo {
    @ApiModelProperty(name="ontid",value = "调用方ontid")
    private String ontid;
    @ApiModelProperty(name="password",value = "调用方密码")
    private String password;
    @ApiModelProperty(name="contractHash",value = "合约hash地址")
    private String contractHash;
    @ApiModelProperty(name="method",value = "合约方法名")
    private String method;
    @ApiModelProperty(name="argsList",value = "参数列表")
    private List argsList;
}
