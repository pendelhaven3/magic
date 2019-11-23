package com.pj.magic.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	@Bean
	@ConfigurationProperties(prefix = "database")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
}
