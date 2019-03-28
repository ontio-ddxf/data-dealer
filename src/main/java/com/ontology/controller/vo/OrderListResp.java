package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderListResp {
    @ApiModelProperty(name="orderId",value = "orderId")
    private String orderId;
    @ApiModelProperty(name="dataDemander",value = "dataDemander")
    private String dataDemander;
    @ApiModelProperty(name="dataProvider",value = "dataProvider")
    private String dataProvider;
    @ApiModelProperty(name="dataIdList",value = "dataIdList")
    private List<String> dataIdList;
    @ApiModelProperty(name="buyDate",value = "buyDate")
    private Date buyDate;
    @ApiModelProperty(name="state",value = "state")
    private String state;
}
