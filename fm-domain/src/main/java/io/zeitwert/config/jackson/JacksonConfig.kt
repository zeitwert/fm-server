package io.zeitwert.config.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class JacksonConfig {

	/**
	 * Standard ObjectMapper This mapper typically does not have any special type discriminators or
	 * modules.
	 */
	@Bean
	@Primary
	@Qualifier("std")
	open fun stdObjectMapper(
		@Qualifier("std") modules: List<Module>,
	): ObjectMapper =
		jacksonObjectMapper().apply {
			registerModule(JavaTimeModule())
			setDateFormat(StdDateFormat())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			modules.forEach { registerModule(it) }
		}

	/**
	 * Standard ObjectMapper that excludes null values during serialization This mapper is identical
	 * to stdObjectMapper but excludes null values from JSON output
	 */
	@Bean
	@Qualifier("stdNonNull")
	open fun stdNonNullObjectMapper(
		@Qualifier("std") modules: List<Module>,
	): ObjectMapper =
		jacksonObjectMapper().apply {
			registerModule(JavaTimeModule())
			setDateFormat(StdDateFormat())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			setSerializationInclusion(JsonInclude.Include.NON_NULL)
			modules.forEach { registerModule(it) }
		}

	/**
	 * UI ObjectMapper This mapper typically has a type field to distinguish between different types
	 * of objects.
	 */
	@Bean
	@Qualifier("ui")
	open fun uiObjectMapper(
		@Qualifier("ui") modules: List<Module>,
	): ObjectMapper =
		jacksonObjectMapper().apply {
			registerModule(JavaTimeModule())
			setDateFormat(StdDateFormat())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			modules.forEach { registerModule(it) }
		}

	/**
	 * Config ObjectMapper This mapper typically uses custom algorithms to discriminate between
	 * different types of objects. For example, by detecting the presence of certain fields. This in
	 * order to make the JSON more editor friendly.
	 */
	@Bean
	@Qualifier("config")
	open fun configObjectMapper(
		@Qualifier("config") modules: List<Module>,
	): ObjectMapper =
		jacksonObjectMapper().apply {
			registerModule(JavaTimeModule())
			setDateFormat(StdDateFormat())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
			modules.forEach { registerModule(it) }
		}

	/** Database ObjectMapper This mapper typically does not use special type discriminators. */
	@Bean
	@Qualifier("db")
	open fun dbObjectMapper(
		@Qualifier("db") modules: List<Module>,
	): ObjectMapper =
		jacksonObjectMapper().apply {
			registerModule(JavaTimeModule())
			setDateFormat(StdDateFormat())
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			modules.forEach { registerModule(it) }
		}

}
