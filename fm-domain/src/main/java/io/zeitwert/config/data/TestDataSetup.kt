package io.zeitwert.config.data

import io.zeitwert.config.DataSetup
import io.zeitwert.config.dsl.Tenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/** Test data setup - provides test tenant and users via repositories. */
@Lazy
@Component
@ConditionalOnProperty(name = ["zeitwert.install_test_data"], havingValue = "true")
class TestDataSetup(
	private val tenantRepository: ObjTenantRepository,
	private val userRepository: ObjUserRepository,
) : DataSetup {

	companion object {

		const val TEST_TENANT_KEY = "test"
		const val TEST_USER_EMAIL = "tt@zeitwert.io"
	}

	override val name = "TEST"
	override val location = "classpath:db/V1.0/5-test"

	override fun setup() {
		println("\nTEST DATA SETUP")
		println("  Setting up test tenant and users...")

		Tenant.init(tenantRepository, userRepository)

		Tenant(TEST_TENANT_KEY, "Test", "advisor") {
			user(TEST_USER_EMAIL, "Tony Testeroni", "user", "test")
			user("cc@zeitwert.io", "Chuck Checkeroni", "user", "test")
		}

		println("  Test data setup complete.\n")
	}

}
