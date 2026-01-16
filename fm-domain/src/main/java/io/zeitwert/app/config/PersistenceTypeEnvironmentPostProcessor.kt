package io.zeitwert.app.config

import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

class PersistenceTypeEnvironmentPostProcessor : EnvironmentPostProcessor, Ordered {

	override fun postProcessEnvironment(
		environment: ConfigurableEnvironment,
		application: org.springframework.boot.SpringApplication?
	) {
		val persistenceType = environment.getProperty("zeitwert.persistence.type") ?: return
		if (!persistenceType.equals("mem", ignoreCase = true)) {
			return
		}

		val excludes = linkedSetOf<String>()
		val existing = environment.getProperty("spring.autoconfigure.exclude")
		if (!existing.isNullOrBlank()) {
			existing.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach(excludes::add)
		}

		excludes.add(FlywayAutoConfiguration::class.java.name)
		excludes.add(JooqAutoConfiguration::class.java.name)

		val propertySource =
			MapPropertySource(
				"zeitwertPersistenceAutoconfigExclude",
				mapOf("spring.autoconfigure.exclude" to excludes.joinToString(","))
			)
		environment.propertySources.addFirst(propertySource)
	}

	override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}
