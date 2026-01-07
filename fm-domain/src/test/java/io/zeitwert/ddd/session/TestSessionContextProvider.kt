package io.zeitwert.ddd.session

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.app.model.impl.SessionContextImpl
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeLocale.Enumeration.getLocale
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.context.annotation.SessionScope

@Configuration
@Profile("test")
open class TestSessionContextProvider {

	@Bean
	@SessionScope
	open fun getSessionContext(
		userRepository: ObjUserRepository,
		accountRepository: ObjAccountRepository,
	): SessionContext {
		val maybeUser = userRepository.getByEmail(userEmail)
		if (maybeUser.isEmpty) {
			throw RuntimeException("Authentication error (unknown user $userEmail)")
		}
		val user = maybeUser.get()
		val maybeAccount = accountRepository.getByKey(accountKey)
		if (maybeAccount.isEmpty) {
			throw RuntimeException("Authentication error (unknown account $accountKey)")
		}
		val account = maybeAccount.get()
		return SessionContextImpl(
			tenantId = user.tenantId,
			user = user,
			accountId = account.id as Int,
			locale = getLocale("en-US")!!,
		)
	}

	companion object {

		const val userEmail: String = "tt@zeitwert.io"
		const val accountKey: String = "TA"
	}

}
