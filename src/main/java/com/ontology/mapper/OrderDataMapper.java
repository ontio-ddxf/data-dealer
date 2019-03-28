package com.ontology.mapper;

import com.ontology.dao.OrderData;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Component
public interface OrderDataMapper extends Mapper<OrderData> {
    void insertList(List<OrderData> ods);
}
