package com.ontology.mapper;

import com.ontology.dao.SMS;

import org.springframework.stereotype.Component;

import tk.mybatis.mapper.common.Mapper;

@Component
public interface SMSMapper extends Mapper<SMS> {
}