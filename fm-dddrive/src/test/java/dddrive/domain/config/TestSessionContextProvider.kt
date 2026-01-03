package dddrive.domain.config

import dddrive.app.ddd.model.SessionContext
import dddrive.domain.ddd.model.impl.TestSessionContextImpl
import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.model.ObjUserRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
@DependsOn("oeBootstrap")
open class TestSessionContextProvider {

	@Bean
	open fun getSessionContext(
		tenantRepository: ObjTenantRepository,
		userRepository: ObjUserRepository,
	): SessionContext {
		val tenant = tenantRepository.getByKey(ObjTenantRepository.KERNEL_TENANT_KEY).orElse(null)
		assertNotNull(tenant, "kTenant")
		val user = userRepository.getByEmail(ObjUserRepository.KERNEL_USER_EMAIL).orElse(null)
		assertNotNull(user, "kUser")
		return TestSessionContextImpl(
			tenantId = user!!.tenantId,
			userId = user.id,
			accountId = null,
		)
	}

}
