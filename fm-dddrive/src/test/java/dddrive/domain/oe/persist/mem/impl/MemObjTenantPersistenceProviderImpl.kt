package dddrive.domain.oe.persist.mem.impl

import dddrive.domain.obj.persist.mem.base.MemObjPersistenceProviderBase
import dddrive.domain.obj.persist.mem.pto.ObjMetaPto
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import dddrive.domain.oe.persist.mem.pto.ObjTenantPto
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
			aggregate.key = pto.key
			aggregate.name = pto.name
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
			key = aggregate.key,
			name = aggregate.name,
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
