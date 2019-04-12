package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EncryptVo {
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="kid",value = "kid")
    private Integer kid;
    @ApiModelProperty(name="message",value = "message")
    private String message;
}
