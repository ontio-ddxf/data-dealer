package com.ontology;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.ontology.secure.SecureHttpMessageConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@tk.mybatis.spring.annotation.MapperScan("com.ontology.mapper")
@EnableTransactionManagement
@EnableSwagger2
//@EnableAspectJAutoProxy
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class OntidManageApplication {

	@Autowired
	SecureHttpMessageConverter secureHttpMessageConverter;

	public static void main(String[] args) {
		SpringApplication.run(OntidManageApplication.class, args);
	}

	@Bean
	public HttpMessageConverters customConverters() {

		final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.indentOutput(true)
				.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

		HttpMessageConverter<?> mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(builder.build());
		return new HttpMessageConverters(secureHttpMessageConverter
				, mappingJackson2HttpMessageConverter);
	}
}
