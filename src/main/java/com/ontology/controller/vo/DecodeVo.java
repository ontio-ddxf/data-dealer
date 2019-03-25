package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DecodeVo {
    @ApiModelProperty(name="dataDemander",value = "数据需求方ontid")
    private String dataDemander;
    @ApiModelProperty(name="password",value = "数据需求方密码")
    private String password;
    @ApiModelProperty(name="message",value = "加密信息")
    private List<String> message;
}
