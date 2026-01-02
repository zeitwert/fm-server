package io.zeitwert.fm.server.config.crnk

import org.springframework.context.annotation.Configuration

/**
 * Marker configuration for Crnk servlet setup.
 *
 * The actual servlet filter configuration is handled in [CrnkConfiguration]
 * which manually configures CrnkBoot and CrnkFilter for Spring Boot 3 compatibility.
 */
@Configuration
open class CrnkServletConfiguration
