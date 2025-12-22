package io.dddrive.domain.oe.persist.mem.impl

import io.dddrive.oe.model.ObjTenant
import io.dddrive.dddrive.obj.persist.mem.base.MemObjPersistenceProviderBase
import io.dddrive.dddrive.obj.persist.mem.pto.ObjMetaPto
import io.dddrive.domain.oe.model.ObjTenantRepository
import io.dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import io.dddrive.domain.oe.persist.mem.pto.ObjTenantPto
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

@Component("objTenantPersistenceProvider")
class MemObjTenantPersistenceProviderImpl :
	MemObjPersistenceProviderBase<ObjTenant, ObjTenantPto>(ObjTenant::class.java),
	ObjTenantPersistenceProvider {

	override fun initKernelTenant(
		tenantId: Any,
		userId: Any,
	) {
		val meta =
			ObjMetaPto(
				maxPartId = 0,
				version = 0,
				ownerId = userId as? Int,
				createdAt = OffsetDateTime.now(),
				createdByUserId = userId as? Int,
				modifiedAt = OffsetDateTime.now(),
				modifiedByUserId = userId as? Int,
				objTypeId = "objTenant",
			)

		val kernelTenant =
			ObjTenantPto(
				id = tenantId as? Int,
				tenantId = tenantId as? Int,
				meta = meta,
				caption = "Kernel",
				name = "Kernel",
				key = ObjTenantRepository.KERNEL_TENANT_KEY,
			)
		this.store(kernelTenant)
	}

	override fun toAggregate(
		pto: ObjTenantPto,
		aggregate: ObjTenant,
	) {
		val aggregateMeta = aggregate.meta
		try {
			aggregateMeta.disableCalc()

			super.toAggregate(pto, aggregate)

			aggregate.name = pto.name
			aggregate.description = pto.description
			aggregate.key = pto.key
		} finally {
			aggregateMeta.enableCalc()
			aggregate.meta.calcAll()
		}
	}

	override fun fromAggregate(aggregate: ObjTenant): ObjTenantPto =
		ObjTenantPto(
			// Properties from AggregatePto
			id = aggregate.id as? Int,
			tenantId = aggregate.tenantId as? Int,
			meta = this.getMeta(aggregate),
			caption = aggregate.caption,
			// Properties specific to ObjTenantPto
			name = aggregate.name,
			description = aggregate.description,
			key = aggregate.key,
		)

	fun getAllPtos(): List<ObjTenantPto> =
		this.aggregates.values
			.filterIsInstance<ObjTenantPto>() // Safely filter and cast
			.toList()

	override fun getByKey(key: String): Optional<Any> {
		val pto =
			this
				.getAllPtos()
				.firstOrNull { it.key == key }
		return Optional.ofNullable(pto?.id)
	}

}
