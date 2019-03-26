package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SellVo {
    @ApiModelProperty(name="dataProvider",value = "数据提供方ontid")
    private String dataProvider;
    @ApiModelProperty(name="password",value = "数据提供方密码")
    private String password;
    @ApiModelProperty(name="orderId",value = "订单id")
    private String orderId;
    @ApiModelProperty(name="encMessageList",value = "数据列表")
    private List encMessageList;
}
