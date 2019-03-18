package com.ontology;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

	/**
	 *  SDK参数
	 */
	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	@Value("${service.payer.address}")
	public String PAYER_ADDRESS;


	/**
	 * paasoo参数
	 */
	@Value("${paasoo.sms.sender}")
	public String PS_SMS_SENDER;

	/**
	 * sendcloud参数
	 */
//	@Value("${sendcloud.email.sender}")
//	public String SC_EMAIL_SENDER;
//
//	@Value("${sendcloud.email.senderName}")
//	public String SC_EMAIL_SENDERNAME;
//
//	@Value("${sendcloud.email.verification.template}")
//	public String SC_EMAIL_VER_TEMPLATE;


	@Value("${sendcloud.sms.verification.template.cn}")
	public String SC_SMS_VER_TEMPLATE_CN;

	@Value("${sendcloud.sms.verification.template.en}")
	public String SC_SMS_VER_TEMPLATE_EN;

}