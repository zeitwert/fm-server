package io.zeitwert.jsonapi.config

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
import org.springframework.context.annotation.Profile
import java.io.IOException

/**
 * Test-specific Crnk configuration for MockMvc tests.
 *
 * This provides the JSON:API infrastructure for testing in fm-domain
 * without depending on fm-server.
 */
@Configuration
@Profile("test")
open class TestCrnkConfiguration {

	@Value("\${crnk.path-prefix:/api}")
	private val pathPrefix: String = "/api"

	@Value("\${crnk.default-page-limit:50}")
	private val defaultPageLimit: Long = 50

	@Value("\${crnk.max-page-limit:1000}")
	private val maxPageLimit: Long = 1000

	@Bean
	open fun crnkBoot(applicationContext: ApplicationContext): CrnkBoot {
		val boot = CrnkBoot()

		val serviceDiscovery = SpringServiceDiscovery()
		serviceDiscovery.setApplicationContext(applicationContext)
		boot.setServiceDiscovery(serviceDiscovery)

		boot.webPathPrefix = pathPrefix

		boot.setPropertiesProvider(
			PropertiesProvider { key: String? ->
				when (key) {
					CrnkProperties.WEB_PATH_PREFIX -> pathPrefix
					CrnkProperties.ALLOW_UNKNOWN_ATTRIBUTES -> "true"
					CrnkProperties.RETURN_404_ON_NULL -> "true"
					else -> null
				}
			},
		)

		boot.setDefaultPageLimit(defaultPageLimit)
		boot.setMaxPageLimit(maxPageLimit)

		val simpleModule = SimpleModule("zeitwert-test")
		boot.addModule(simpleModule)

		val objectMapper = boot.getObjectMapper()
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		objectMapper.registerModule(JavaTimeModule())
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

		boot.boot()

		return boot
	}

	@Bean
	open fun crnkFilter(crnkBoot: CrnkBoot): Filter {
		return TestCrnkFilter(crnkBoot, pathPrefix)
	}

	/**
	 * Jakarta Servlet API compatible filter for Crnk.
	 */
	private class TestCrnkFilter(
		private val crnkBoot: CrnkBoot,
		private val pathPrefix: String,
	) : Filter {

		@Throws(IOException::class, ServletException::class)
		override fun doFilter(
			request: ServletRequest,
			response: ServletResponse,
			chain: FilterChain,
		) {
			if (request is HttpServletRequest && response is HttpServletResponse) {
				var path = request.requestURI
				val contextPath = request.contextPath
				if (!contextPath.isNullOrEmpty()) {
					path = path.substring(contextPath.length)
				}

				if (path.startsWith(pathPrefix)) {
					val requestContext = TestHttpRequestContext(request, response, pathPrefix)
					try {
						crnkBoot.requestDispatcher.process(requestContext)
					} catch (ex: Exception) {
						throw ServletException("Error processing Crnk request", ex)
					}
					if (requestContext.hasResponse()) {
						requestContext.flushResponse()
						return
					}
				}
			}

			chain.doFilter(request, response)
		}
	}
}
