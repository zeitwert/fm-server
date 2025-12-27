package dddrive.app.obj.model.base

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.ObjRepository
import dddrive.app.obj.model.impl.ObjPartTransitionImpl
import dddrive.ddd.core.model.base.AggregateRepositoryBase
import java.time.OffsetDateTime

abstract class ObjRepositoryBase<O : Obj>(
	intfClass: Class<out Obj>,
	aggregateTypeId: String,
) : dddrive.ddd.core.model.base.AggregateRepositoryBase<O>(intfClass, aggregateTypeId),
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
