package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RegisterVo {
    @ApiModelProperty(name="phone",value = "手机号码")
    private String phone;
    @ApiModelProperty(name="password",value = "密码")
    private String password;
}
