package com.ontology.mapper;

import com.ontology.dao.Order;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface OrderMapper extends Mapper<Order> {
}
