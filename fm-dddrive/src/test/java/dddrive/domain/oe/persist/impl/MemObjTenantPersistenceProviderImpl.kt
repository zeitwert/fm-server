package dddrive.domain.oe.persist.impl

import dddrive.db.MemoryDb
import dddrive.domain.obj.persist.base.MemObjPersistenceProviderBase
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import dddrive.query.query
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

/**
 * Map-based persistence provider for ObjTenant.
 *
 * Active when persistence.type=map
 */
@Component("objTenantPersistenceProvider")
class MemObjTenantPersistenceProviderImpl :
	MemObjPersistenceProviderBase<ObjTenant>(ObjTenant::class.java),
	ObjTenantPersistenceProvider {

	override fun initKernelTenant(
		tenantId: Any,
		userId: Any,
	) {
		val kernelTenantMap = mapOf(
			"id" to tenantId,
			"tenantId" to tenantId,
			"version" to 0,
			"maxPartId" to 0,
			"ownerId" to userId,
			"createdAt" to OffsetDateTime.now(),
			"createdByUserId" to userId,
			"modifiedAt" to OffsetDateTime.now(),
			"modifiedByUserId" to userId,
			"caption" to "Kernel",
			"name" to "Kernel",
			"key" to ObjTenantRepository.KERNEL_TENANT_KEY,
		)
		this.store(kernelTenantMap)
	}

	override fun getByKey(key: String): Optional<Any> {
		val tenantMap =
			MemoryDb
				.find(intfClass, query { filter { "key" eq key } })
				.firstOrNull()
		return Optional.ofNullable(tenantMap?.get("id"))
	}

}


