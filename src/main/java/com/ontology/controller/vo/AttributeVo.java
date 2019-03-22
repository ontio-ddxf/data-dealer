package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttributeVo {
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="password",value = "password")
    private String password;
    @ApiModelProperty(name="key",value = "key")
    private String key;
    @ApiModelProperty(name="valueType",value = "valueType")
    private String valueType;
    @ApiModelProperty(name="value",value = "value")
    private String value;
}
