package io.dddrive.core.obj.model.base

import io.dddrive.core.ddd.model.AggregateRepository
import io.dddrive.core.ddd.model.base.AggregateRepositoryBase
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjPartTransition
import io.dddrive.core.obj.model.ObjRepository
import java.time.OffsetDateTime

abstract class ObjRepositoryBase<O : Obj>(
	repoIntfClass: Class<out AggregateRepository<O>>,
	intfClass: Class<out Obj>,
	baseClass: Class<out Obj>,
	aggregateTypeId: String,
) : AggregateRepositoryBase<O>(repoIntfClass, intfClass, baseClass, aggregateTypeId),
	ObjRepository<O> {

	override fun doLogChange(property: String): Boolean {
		if (NotLoggedProperties.contains(property)) {
			return false
		}
		return super.doLogChange(property)
	}

	override fun registerParts() {
		this.addPart<Obj>(Obj::class.java, ObjPartTransition::class.java, ObjPartTransitionBase::class.java)
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

		private val NotLoggedProperties =
			mutableSetOf<String?>("objTypeId", "closedByUser", "closedAt", "transitionList")
	}

}
