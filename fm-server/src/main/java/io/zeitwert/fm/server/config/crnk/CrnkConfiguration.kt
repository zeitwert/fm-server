package io.zeitwert.fm.server.config.crnk

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.crnk.core.boot.CrnkBoot
import io.crnk.core.boot.CrnkProperties
import io.crnk.core.engine.properties.PropertiesProvider
import io.crnk.core.module.SimpleModule
import io.crnk.spring.internal.SpringServiceDiscovery
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.util.function.Consumer

/**
 * Manual Crnk configuration for Spring Boot 3.
 *
 * Note: crnk-servlet uses javax.servlet which is incompatible with Spring Boot 3's
 * jakarta.servlet, so we create a custom Jakarta-compatible filter.
 */
@Configuration
open class CrnkConfiguration {

	@Value("\${crnk.path-prefix:/api}")
	private val pathPrefix: String? = null

	@Value("\${crnk.default-page-limit:50}")
	private val defaultPageLimit: Long? = null

	@Value("\${crnk.max-page-limit:1000}")
	private val maxPageLimit: Long? = null

	@Value("\${crnk.allow-unknown-attributes:false}")
	private val allowUnknownAttributes = false

	@Value("\${crnk.return404-on-null:true}")
	private val return404OnNull = false

	@Bean
	open fun crnkBoot(
		applicationContext: ApplicationContext,
		urlMapper: DefaultQuerySpecUrlMapper?,
		configurers: MutableList<CrnkBootConfigurer>,
	): CrnkBoot {
		val boot = CrnkBoot()

		// Set up Spring service discovery for finding Crnk repositories and modules
		val serviceDiscovery = SpringServiceDiscovery()
		serviceDiscovery.setApplicationContext(applicationContext)
		boot.setServiceDiscovery(serviceDiscovery)

		// Configure web path prefix
		boot.webPathPrefix = pathPrefix

		// Configure URL mapper (with custom path resolver)
		boot.setUrlMapper(urlMapper)

		// Configure properties
		boot.setPropertiesProvider(
			PropertiesProvider { key: String? ->
				when (key) {
					CrnkProperties.WEB_PATH_PREFIX -> pathPrefix
					CrnkProperties.ALLOW_UNKNOWN_ATTRIBUTES -> allowUnknownAttributes.toString()
					CrnkProperties.RETURN_404_ON_NULL -> return404OnNull.toString()
					else -> null
				}
			},
		)

		// Configure pagination
		boot.setDefaultPageLimit(defaultPageLimit)
		boot.setMaxPageLimit(maxPageLimit)

		// Register a simple module for any additional setup
		val simpleModule = SimpleModule("zeitwert")
		boot.addModule(simpleModule)

		// Allow configurers to customize CrnkBoot
		configurers.forEach(Consumer { configurer: CrnkBootConfigurer? -> configurer!!.configure(boot) })

		// Configure ObjectMapper for Spring Boot 3.x / Jackson 2.19.x compatibility
		val objectMapper = boot.getObjectMapper()
		// Ignore unknown properties (newer Jackson is stricter about unknown fields)
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		// Register Java 8 date/time module for OffsetDateTime, LocalDate, etc.
		objectMapper.registerModule(JavaTimeModule())
		// Write dates as ISO strings, not timestamps
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

		// Initialize Crnk
		boot.boot()

		return boot
	}

	@Bean
	open fun crnkFilter(crnkBoot: CrnkBoot): Filter {
		// Create a Jakarta-compatible filter that wraps crnk's functionality
		return JakartaCrnkFilter(crnkBoot, pathPrefix)
	}

	/**
	 * Jakarta Servlet API compatible filter for Crnk.
	 *
	 * Since the original CrnkFilter uses javax.servlet (incompatible with Spring Boot 3),
	 * this filter provides the same functionality using jakarta.servlet.
	 */
	@JvmRecord
	private data class JakartaCrnkFilter(
		val crnkBoot: CrnkBoot?,
		val pathPrefix: String?,
	) : Filter {

		@Throws(IOException::class, ServletException::class)
		override fun doFilter(
			request: ServletRequest?,
			response: ServletResponse?,
			chain: FilterChain,
		) {
			if (request is HttpServletRequest &&
				response is HttpServletResponse
			) {
				var path = request.requestURI
				val contextPath = request.contextPath
				if (contextPath != null && !contextPath.isEmpty()) {
					path = path.substring(contextPath.length)
				}

				// Only process requests to the crnk API path
				if (path.startsWith(pathPrefix!!)) {
					// Create and process the request through crnk
					val sessionContext = JakartaHttpRequestContext(request, response, pathPrefix)
					// Process the request using crnk's request dispatcher
					try {
						crnkBoot!!.getRequestDispatcher().process(sessionContext)
					} catch (ex: Exception) {
						throw ServletException("Error processing Crnk request", ex)
					}
					// Check if crnk handled the request
					if (sessionContext.hasResponse()) {
						sessionContext.flushResponse()
						return // Crnk handled the request
					}
				}
			}

			// Pass to next filter if not handled by crnk
			chain.doFilter(request, response)
		}
	}

}
