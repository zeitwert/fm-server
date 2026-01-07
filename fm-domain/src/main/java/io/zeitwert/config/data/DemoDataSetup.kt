package io.zeitwert.config.data

import io.zeitwert.config.DataSetup
import io.zeitwert.config.dsl.Tenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/** Demo data setup - provides demo tenant and users via repositories. */
@Lazy
@Component
@ConditionalOnProperty(name = ["zeitwert.install_demo_data"], havingValue = "true")
class DemoDataSetup(
	private val tenantRepository: ObjTenantRepository,
	private val userRepository: ObjUserRepository,
) : DataSetup {

	companion object {

		const val DEMO_TENANT_KEY = "demo"
		const val DEMO_ADMIN_EMAIL = "admin@zeitwert.io"
	}

	override val name = "DEMO"
	override val location = "classpath:db/V1.0/6-demo"

	override fun setup() {
		println("\nDEMO DATA SETUP")
		println("  Setting up demo tenant and users...")

		Tenant.init(tenantRepository, userRepository)

		Tenant(DEMO_TENANT_KEY, "Demo", "advisor") {
			user(DEMO_ADMIN_EMAIL, "Admin", "admin", "demo")
			user("hannes@zeitwert.io", "Hannes Brunner", "super_user", "demo")
			user("martin@zeitwert.io", "Martin Frey", "super_user", "demo")
			user("urs@zeitwert.io", "Urs Muster", "user", "demo")
		}
		println("  Demo data setup complete.\n")
	}

}
