package com.ontology.mapper;

import com.ontology.dao.Developer;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface DeveloperMapper extends Mapper<Developer> {
}