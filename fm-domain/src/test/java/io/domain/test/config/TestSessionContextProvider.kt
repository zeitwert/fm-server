package io.domain.test.config

import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.app.session.model.impl.SessionContextImpl
import io.zeitwert.data.config.TestDataSetup
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeLocale
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.web.context.annotation.SessionScope

@Configuration
@Primary
@Profile("test")
open class TestSessionContextProvider {

	@Bean
	@SessionScope
	open fun getSessionContext(
		userRepository: ObjUserRepository,
		accountRepository: ObjAccountRepository,
	): SessionContext {
		val maybeUser = userRepository.getByEmail(TestDataSetup.TEST_USER_EMAIL)
		if (maybeUser.isEmpty) {
			throw RuntimeException("Authentication error (unknown user ${TestDataSetup.TEST_USER_EMAIL})")
		}
		val user = maybeUser.get()
		val maybeAccount = accountRepository.getByKey(TestDataSetup.TEST_ACCOUNT_KEY)
		if (maybeAccount.isEmpty) {
			throw RuntimeException("Authentication error (unknown account ${TestDataSetup.TEST_ACCOUNT_KEY})")
		}
		val account = maybeAccount.get()
		return SessionContextImpl(
			tenantId = user.tenantId,
			userId = user.id,
			accountId = account.id as Int,
			locale = CodeLocale.Enumeration.getLocale("en-US")!!,
		)
	}

}
