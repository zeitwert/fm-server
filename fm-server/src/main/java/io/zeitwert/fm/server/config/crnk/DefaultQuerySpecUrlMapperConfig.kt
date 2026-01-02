package io.zeitwert.fm.server.config.crnk

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DefaultQuerySpecUrlMapperConfig {

	@Bean
	open fun defaultQuerySpecUrlMapper(): DefaultQuerySpecUrlMapper {
		// Create mapper with custom path resolver that doesn't map JSON names
		val mapper = DefaultQuerySpecUrlMapper()
		return mapper
	}

}
