package dddrive.domain.obj.persist.mem.base

import dddrive.app.obj.model.Obj
import dddrive.ddd.path.getValueByPath
import dddrive.ddd.path.setValueByPath
import dddrive.domain.ddd.persist.mem.base.MemAggregatePersistenceProviderBase
import dddrive.domain.obj.persist.mem.pto.ObjMetaPto
import dddrive.domain.obj.persist.mem.pto.ObjPartTransitionPto
import dddrive.domain.obj.persist.mem.pto.ObjPto

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
		aggregate.setValueByPath("closedByUserId", objMetaPto?.closedByUserId)
		aggregate.setValueByPath("closedAt", objMetaPto?.closedAt)
		// TODO transitions
	}

	@Suppress("UNCHECKED_CAST")
	protected fun getMeta(aggregate: O): ObjMetaPto {
		val maxPartId = aggregate.getValueByPath("maxPartId") as? Int?
		val transitions = aggregate.meta.transitionList
			.map { t -> ObjPartTransitionPto(id = t.id, userId = t.userId, timestamp = t.timestamp) }
			.toList()
		return ObjMetaPto(
			objTypeId = aggregate.meta.repository.aggregateType.id,
			version = aggregate.meta.version,
			maxPartId = maxPartId,
			ownerId = aggregate.ownerId as? Int,
			createdAt = aggregate.meta.createdAt,
			createdByUserId = aggregate.meta.createdByUserId as Int,
			modifiedAt = aggregate.meta.modifiedAt,
			modifiedByUserId = aggregate.meta.modifiedByUserId as? Int,
			closedAt = aggregate.meta.closedAt,
			closedByUserId = aggregate.meta.closedByUserId as? Int,
			transitions = transitions,
		)
	}
}
