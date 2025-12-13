package io.zeitwert.app

import com.fasterxml.jackson.databind.ObjectMapper
import io.zeitwert.config.jackson.JacksonConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class ObjectMapperConfig(
	private val jacksonConfig: JacksonConfig,
) {
	@Bean
	@Primary
	open fun objectMapper(): ObjectMapper = jacksonConfig.stdObjectMapper(emptyList())
}

