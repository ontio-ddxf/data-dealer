package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuyVo {
    @ApiModelProperty(name="dataDemander",value = "数据需求方ontid")
    private String dataDemander;
    @ApiModelProperty(name="password",value = "数据需求方密码")
    private String password;
    @ApiModelProperty(name="dataProvider",value = "数据提供方ontid")
    private String dataProvider;
    @ApiModelProperty(name="dataIdList",value = "所需数据id列表")
    private List<String> dataIdList;
    @ApiModelProperty(name="priceList",value = "所需数据价格列表")
    private List<Long> priceList;
}
