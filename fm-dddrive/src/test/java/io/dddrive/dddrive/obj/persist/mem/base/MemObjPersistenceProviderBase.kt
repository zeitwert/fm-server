package io.dddrive.dddrive.obj.persist.mem.base

import io.dddrive.core.obj.model.Obj
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.dddrive.ddd.persist.mem.base.MemAggregatePersistenceProviderBase
import io.dddrive.dddrive.obj.persist.mem.pto.ObjMetaPto
import io.dddrive.dddrive.obj.persist.mem.pto.ObjPartTransitionPto
import io.dddrive.dddrive.obj.persist.mem.pto.ObjPto
import java.time.OffsetDateTime

abstract class MemObjPersistenceProviderBase<O : Obj, Pto : ObjPto>(
	intfClass: Class<O>,
) : MemAggregatePersistenceProviderBase<O, Pto>(intfClass) {

	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		pto: Pto,
		aggregate: O,
	) {
		super.toAggregate(pto, aggregate)

		val objMetaPto = pto.meta

		(aggregate.getProperty("objTypeId") as? BaseProperty<String?>)?.value = objMetaPto?.objTypeId
		(aggregate.getProperty("closedByUser") as? ReferenceProperty<ObjUser>)?.id = objMetaPto?.closedByUserId
		(aggregate.getProperty("closedAt") as? BaseProperty<OffsetDateTime?>)?.value = objMetaPto?.closedAt
		// TODO transitions
	}

	@Suppress("UNCHECKED_CAST")
	protected fun getMeta(aggregate: O): ObjMetaPto {
		val maxPartId = (aggregate.getProperty("maxPartId") as? BaseProperty<Int?>)?.value

		// Map transitions from the domain model (ObjPartTransition) to PTO (ObjPartTransitionPto)
		val transitions =
			aggregate.meta.transitionList
				.map { domainTransition ->
					ObjPartTransitionPto(
						id = domainTransition.id,
						userId = domainTransition.user?.id,
						timestamp = domainTransition.timestamp,
					)
				}.toList()

		return ObjMetaPto(
			// Properties specific to ObjMetaPto
			objTypeId = aggregate.meta.repository
				?.aggregateType
				?.id,
			closedAt = aggregate.meta.closedAt,
			closedByUserId = aggregate.meta.closedByUser?.id as? Int,
			transitions = transitions,
			// Properties inherited from AggregateMetaPto, passed to ObjMetaPto's constructor
			maxPartId = maxPartId,
			ownerId = aggregate.owner.id as? Int,
			version = aggregate.meta.version,
			createdAt = aggregate.meta.createdAt,
			createdByUserId = aggregate.meta.createdByUser?.id as? Int,
			modifiedAt = aggregate.meta.modifiedAt,
			modifiedByUserId = aggregate.meta.modifiedByUser?.id as? Int,
		)
	}
}
