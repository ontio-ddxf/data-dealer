package com.ontology.secure;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;

public interface ISecureHttpMessageConverter {
    Object ReadConvert(JavaType javaType, HttpInputMessage inputMessage) throws Exception;

    Object writeConvert(ObjectWriter ow, HttpOutputMessage outputMessage, Object value) throws Exception;
}
