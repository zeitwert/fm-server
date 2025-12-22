package io.dddrive.domain.oe.config

import io.dddrive.ddd.model.AggregateRepositorySPI
import io.dddrive.domain.oe.model.ObjTenantRepository
import io.dddrive.domain.oe.model.ObjUserRepository
import io.dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import io.dddrive.domain.oe.persist.ObjUserPersistenceProvider
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

@Component("oeBootstrap")
@DependsOn("objTenantPersistenceProvider", "objUserPersistenceProvider")
class OeBootstrap : InitializingBean {

	@Autowired
	internal lateinit var tenantRepo: ObjTenantRepository

	@Autowired
	internal lateinit var tenantPersistenceProvider: ObjTenantPersistenceProvider

	@Autowired
	internal lateinit var userRepo: ObjUserRepository

	@Autowired
	internal lateinit var userPersistenceProvider: ObjUserPersistenceProvider

	override fun afterPropertiesSet() {
		println("OE BOOTSTRAP")
		if (this.tenantRepo.getByKey(ObjTenantRepository.KERNEL_TENANT_KEY).isEmpty) {
			val tenantPersistenceProvider =
				(this.tenantRepo as? AggregateRepositorySPI<*>)?.persistenceProvider as? ObjTenantPersistenceProvider
			val userPersistenceProvider =
				(this.userRepo as? AggregateRepositorySPI<*>)?.persistenceProvider as? ObjUserPersistenceProvider

			if (tenantPersistenceProvider == null || userPersistenceProvider == null) {
				System.err.println("OE BOOTSTRAP: Could not obtain persistence providers.")
				return
			}

			val tenantId = tenantPersistenceProvider.nextAggregateId()
			val userId = userPersistenceProvider.nextAggregateId()

			tenantPersistenceProvider.initKernelTenant(tenantId, userId)
			userPersistenceProvider.initKernelUser(tenantId, userId)
		}
	}

}
