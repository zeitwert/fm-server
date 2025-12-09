package io.zeitwert.fm.server.config.crnk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultQuerySpecUrlMapperConfig {

	@Bean
	public DefaultQuerySpecUrlMapper defaultQuerySpecUrlMapper() {
		// Create mapper with custom path resolver that doesn't map JSON names
		DefaultQuerySpecUrlMapper mapper = new DefaultQuerySpecUrlMapper();
		return mapper;
	}

}

