package io.zeitwert.fm.server.config.session

import io.zeitwert.app.session.model.SessionContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.annotation.SessionScope

@Configuration
@Primary
@Profile("dev", "staging", "prod")
open class SessionContextProvider {

	@Bean
	@SessionScope
	open fun getSessionContext(): SessionContext {
		val auth = SecurityContextHolder.getContext().authentication
		check("anonymousUser" != auth.principal) { "Anonymous user is not allowed to access this resource" }
		// Use DelegatingSessionContext which reads dynamically from SecurityContextHolder
		// This ensures that reactivation with a new account is properly reflected
		return DynamicSessionContext()
	}

}
