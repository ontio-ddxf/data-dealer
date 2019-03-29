package com.ontology.mapper;

import com.ontology.dao.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Component
public interface OrderMapper extends Mapper<Order> {
    List<Order> getBuyerList(@Param("queryType") String queryType, @Param("ontid")String buyerOntid);
}
