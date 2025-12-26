package io.dddrive.obj.model.base

import io.dddrive.ddd.model.base.AggregateRepositoryBase
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjPartTransition
import io.dddrive.obj.model.ObjRepository
import io.dddrive.obj.model.impl.ObjPartTransitionImpl
import java.time.OffsetDateTime

abstract class ObjRepositoryBase<O : Obj>(
	intfClass: Class<out Obj>,
	aggregateTypeId: String,
) : AggregateRepositoryBase<O>(intfClass, aggregateTypeId),
	ObjRepository<O> {

	override fun doLogChange(property: String): Boolean {
		if (NotLoggedProperties.contains(property)) {
			return false
		}
		return super.doLogChange(property)
	}

	override fun registerParts() {
		this.addPart(ObjPartTransition::class.java, ::ObjPartTransitionImpl)
	}

	override fun close(
		obj: O,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		obj.delete(userId, timestamp)
		this.store(obj, userId, timestamp)
	}

	companion object {

		private val NotLoggedProperties = setOf("closedByUser", "closedAt", "transitionList")
	}

}
