package com.ontology.mapper;

import com.ontology.dao.Event;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface EventMapper extends Mapper<Event> {
}
