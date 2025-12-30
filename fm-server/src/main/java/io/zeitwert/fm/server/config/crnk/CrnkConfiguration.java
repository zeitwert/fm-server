package io.zeitwert.fm.server.config.crnk;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.boot.CrnkProperties;
import io.crnk.core.module.SimpleModule;
import io.crnk.spring.internal.SpringServiceDiscovery;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * Manual Crnk configuration for Spring Boot 3.
 * <p>
 * Since crnk-setup-spring-boot doesn't support Spring Boot 3, we configure
 * CrnkBoot manually using crnk-core and crnk-setup-spring modules.
 * <p>
 * Note: crnk-servlet uses javax.servlet which is incompatible with Spring Boot 3's
 * jakarta.servlet, so we create a custom Jakarta-compatible filter.
 */
@Configuration
public class CrnkConfiguration {

	@Value("${crnk.path-prefix:/api}")
	private String pathPrefix;

	@Value("${crnk.default-page-limit:50}")
	private Long defaultPageLimit;

	@Value("${crnk.max-page-limit:1000}")
	private Long maxPageLimit;

	@Value("${crnk.allow-unknown-attributes:false}")
	private boolean allowUnknownAttributes;

	@Value("${crnk.return404-on-null:true}")
	private boolean return404OnNull;

	@Bean
	public CrnkBoot crnkBoot(ApplicationContext applicationContext,
													 DefaultQuerySpecUrlMapper urlMapper,
													 List<CrnkBootConfigurer> configurers) {

		CrnkBoot boot = new CrnkBoot();

		// Set up Spring service discovery for finding Crnk repositories and modules
		SpringServiceDiscovery serviceDiscovery = new SpringServiceDiscovery();
		serviceDiscovery.setApplicationContext(applicationContext);
		boot.setServiceDiscovery(serviceDiscovery);

		// Configure web path prefix
		boot.setWebPathPrefix(pathPrefix);

		// Configure URL mapper (with custom path resolver)
		boot.setUrlMapper(urlMapper);

		// Configure properties
		boot.setPropertiesProvider(key -> {
			return switch (key) {
				case CrnkProperties.WEB_PATH_PREFIX -> pathPrefix;
				case CrnkProperties.ALLOW_UNKNOWN_ATTRIBUTES -> String.valueOf(allowUnknownAttributes);
				case CrnkProperties.RETURN_404_ON_NULL -> String.valueOf(return404OnNull);
				default -> null;
			};
		});

		// Configure pagination
		boot.setDefaultPageLimit(defaultPageLimit);
		boot.setMaxPageLimit(maxPageLimit);

		// Register a simple module for any additional setup
		SimpleModule simpleModule = new SimpleModule("zeitwert");
		boot.addModule(simpleModule);

		// Allow configurers to customize CrnkBoot
		configurers.forEach(configurer -> configurer.configure(boot));

		// Configure ObjectMapper for Spring Boot 3.x / Jackson 2.19.x compatibility
		ObjectMapper objectMapper = boot.getObjectMapper();
		// Ignore unknown properties (newer Jackson is stricter about unknown fields)
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// Register Java 8 date/time module for OffsetDateTime, LocalDate, etc.
		objectMapper.registerModule(new JavaTimeModule());
		// Write dates as ISO strings, not timestamps
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		// Initialize Crnk
		boot.boot();

		return boot;
	}

	@Bean
	public Filter crnkFilter(CrnkBoot crnkBoot) {
		// Create a Jakarta-compatible filter that wraps crnk's functionality
		return new JakartaCrnkFilter(crnkBoot, pathPrefix);
	}

	/**
	 * Jakarta Servlet API compatible filter for Crnk.
	 * <p>
	 * Since the original CrnkFilter uses javax.servlet (incompatible with Spring Boot 3),
	 * this filter provides the same functionality using jakarta.servlet.
	 */
		private record JakartaCrnkFilter(CrnkBoot crnkBoot, String pathPrefix) implements Filter {

		@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {

				if (request instanceof HttpServletRequest httpRequest
						&& response instanceof HttpServletResponse httpResponse) {

					String path = httpRequest.getRequestURI();
					String contextPath = httpRequest.getContextPath();
					if (contextPath != null && !contextPath.isEmpty()) {
						path = path.substring(contextPath.length());
					}

					// Only process requests to the crnk API path
					if (path.startsWith(pathPrefix)) {
						// Create and process the request through crnk
						JakartaHttpRequestContext sessionContext = new JakartaHttpRequestContext(
								httpRequest, httpResponse, pathPrefix);

						// Process the request using crnk's request dispatcher
						crnkBoot.getRequestDispatcher().process(sessionContext);

						// Check if crnk handled the request
						if (sessionContext.hasResponse()) {
							sessionContext.flushResponse();
							return; // Crnk handled the request
						}
					}
				}

				// Pass to next filter if not handled by crnk
				chain.doFilter(request, response);
			}

		}

}
