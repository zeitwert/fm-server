package dddrive.domain.oe.persist.mem.impl

import dddrive.domain.obj.persist.mem.base.MemObjPersistenceProviderBase
import dddrive.domain.obj.persist.mem.pto.ObjMetaPto
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository
import dddrive.domain.oe.persist.ObjUserPersistenceProvider
import dddrive.domain.oe.persist.mem.pto.ObjUserPto
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

@Component("objUserPersistenceProvider")
@DependsOn("objTenantPersistenceProvider")
class MemObjUserPersistenceProviderImpl :
	MemObjPersistenceProviderBase<ObjUser, ObjUserPto>(ObjUser::class.java),
	ObjUserPersistenceProvider {

	override fun initKernelUser(
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

		val kUser =
			ObjUserPto(
				id = userId as? Int,
				tenantId = tenantId as? Int,
				meta = meta,
				email = ObjUserRepository.KERNEL_USER_EMAIL,
				caption = "Kernel User",
				name = "Kernel User",
			)
		this.store(kUser)
	}

	override fun toAggregate(
		pto: ObjUserPto,
		aggregate: ObjUser,
	) {
		try {
			aggregate.meta.disableCalc()
			super.toAggregate(pto, aggregate)
			aggregate.email = pto.email
			aggregate.name = pto.name
		} finally {
			aggregate.meta.enableCalc()
			aggregate.meta.calcAll()
		}
	}

	override fun fromAggregate(aggregate: ObjUser): ObjUserPto =
		ObjUserPto(
			// Properties from AggregatePto
			id = aggregate.id as? Int,
			tenantId = aggregate.tenantId as? Int,
			meta = this.getMeta(aggregate),
			caption = aggregate.caption,
			// Properties specific to ObjUserPto
			email = aggregate.email,
			name = aggregate.name,
		)

	fun getAllPtos(): List<ObjUserPto> =
		this.aggregates.values
			.filterIsInstance<ObjUserPto>()
			.toList()

	override fun getByEmail(email: String): Optional<Any> {
		val pto =
			this
				.getAllPtos()
				.firstOrNull { it.email == email }
		return Optional.ofNullable(pto?.id)
	}

}
