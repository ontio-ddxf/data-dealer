package com.onchain.mapper;

import com.onchain.dao.Order;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface OrderMapper extends Mapper<Order> {
}
