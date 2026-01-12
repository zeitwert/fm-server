package io.zeitwert.persist.mem

import dddrive.db.MemoryDb
import dddrive.query.query
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.persist.ObjTenantPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*
import kotlin.jvm.java

/**
 * Memory-based persistence provider for ObjTenant.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objTenantPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjTenantMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjTenant>(ObjTenant::class.java),
	ObjTenantPersistenceProvider {

	init {
		val tenantId = nextAggregateId()
		val userId = nextAggregateId()
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
		MemoryDb.store(ObjTenant::class.java, kernelTenantMap)
		val kernelUserMap = mapOf(
			"id" to userId,
			"tenantId" to tenantId,
			"version" to 0,
			"maxPartId" to 0,
			"ownerId" to userId,
			"createdAt" to OffsetDateTime.now(),
			"createdByUserId" to userId,
			"modifiedAt" to OffsetDateTime.now(),
			"modifiedByUserId" to userId,
			"email" to ObjUserRepository.KERNEL_USER_EMAIL,
			"caption" to "Kernel User",
			"name" to "Kernel User",
		)
		MemoryDb.store(ObjUser::class.java, kernelUserMap)
	}

	override fun getByKey(key: String): Optional<Any> {
		val tenantMap =
			MemoryDb
				.find(intfClass, query { filter { "key" eq key } })
				.firstOrNull()
		return Optional.ofNullable(tenantMap?.get("id"))
	}

}
