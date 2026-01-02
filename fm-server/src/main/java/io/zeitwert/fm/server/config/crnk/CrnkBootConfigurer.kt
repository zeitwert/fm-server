package io.zeitwert.fm.server.config.crnk

import io.crnk.core.boot.CrnkBoot

/**
 * Local interface to replace io.crnk.spring.setup.boot.core.CrnkBootConfigurer
 * which is not available in Spring Boot 3 (crnk-setup-spring-boot doesn't support SB3).
 *
 * Implement this interface to customize CrnkBoot configuration.
 */
fun interface CrnkBootConfigurer {

	fun configure(boot: CrnkBoot)

}
