package com.ontology.mapper;

import com.ontology.dao.OntId;

import org.springframework.stereotype.Component;

import tk.mybatis.mapper.common.Mapper;

@Component
public interface OntIdMapper extends Mapper<OntId> {
}