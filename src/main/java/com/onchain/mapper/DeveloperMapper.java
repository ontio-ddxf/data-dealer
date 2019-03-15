package com.onchain.mapper;

import com.onchain.dao.Developer;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface DeveloperMapper extends Mapper<Developer> {
}