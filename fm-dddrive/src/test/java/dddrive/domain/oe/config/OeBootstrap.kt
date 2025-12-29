package dddrive.domain.oe.config

import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import dddrive.domain.oe.persist.ObjUserPersistenceProvider
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("oeBootstrap")
class OeBootstrap : InitializingBean {

	@Autowired
	internal lateinit var tenantPersistenceProvider: ObjTenantPersistenceProvider

	@Autowired
	internal lateinit var userPersistenceProvider: ObjUserPersistenceProvider

	override fun afterPropertiesSet() {
		println("OE BOOTSTRAP")
		// Use persistence provider directly instead of repository to avoid circular dependency
		if (tenantPersistenceProvider.getByKey(ObjTenantRepository.KERNEL_TENANT_KEY).isEmpty) {
			val tenantId = tenantPersistenceProvider.nextAggregateId()
			val userId = userPersistenceProvider.nextAggregateId()

			tenantPersistenceProvider.initKernelTenant(tenantId, userId)
			userPersistenceProvider.initKernelUser(tenantId, userId)
		}
	}

}
