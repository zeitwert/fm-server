package io.zeitwert.config.data

import io.zeitwert.config.DataSetup
import io.zeitwert.config.dsl.Account
import io.zeitwert.config.dsl.Tenant
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/** Test data setup - provides test tenant, users, accounts, and contacts via repositories. */
@Lazy
@Component
@ConditionalOnProperty(name = ["zeitwert.install_test_data"], havingValue = "true")
class TestDataSetup(
	private val tenantRepository: ObjTenantRepository,
	private val userRepository: ObjUserRepository,
	private val accountRepository: ObjAccountRepository,
	private val contactRepository: ObjContactRepository,
) : DataSetup {

	companion object {

		const val TEST_TENANT_KEY = "test"
		const val TEST_USER_EMAIL = "tt@zeitwert.io"
		const val TEST_ACCOUNT_KEY = "TA"
	}

	override val name = "TEST"

	override fun setup() {
		println("\nTEST DATA SETUP")
		println("  Setting up test tenant and users...")

		Tenant.init(tenantRepository, userRepository)

		Tenant(TEST_TENANT_KEY, "Test", "advisor") {
			user(TEST_USER_EMAIL, "Tony Testeroni", "user", "test")
			user("cc@zeitwert.io", "Chuck Checkeroni", "user", "test")
		}

		println("  Setting up test accounts and contacts...")

		Account.init(accountRepository, contactRepository)

		Account(TEST_ACCOUNT_KEY, "Testlingen", "client")

		println("  Test data setup complete.\n")
	}

}
