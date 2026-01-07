package io.zeitwert.fm.server.session.service.api.impl

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.app.model.impl.SessionContextImpl
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeLocale
import io.zeitwert.fm.server.config.security.AppUserDetails
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
	open fun getSessionContext(userRepository: ObjUserRepository): SessionContext {
		val auth = SecurityContextHolder.getContext().authentication
		check("anonymousUser" != auth.principal) { "Anonymous user is not allowed to access this resource" }

		val userDetails = auth.principal as AppUserDetails
		val user = userRepository.get(userDetails.userId)
		val tenantId = userDetails.tenantId
		val accountId = userDetails.accountId

		return SessionContextImpl(
			tenantId = tenantId,
			userId = user.id,
			accountId = accountId,
			locale = CodeLocale.DE_CH, // ,
		)
	}

}
