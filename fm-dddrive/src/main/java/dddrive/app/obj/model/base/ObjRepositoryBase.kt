package dddrive.app.obj.model.base

import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPartTransition
import dddrive.app.obj.model.ObjRepository
import dddrive.app.obj.model.impl.ObjPartTransitionImpl
import dddrive.ddd.core.model.base.AggregateRepositoryBase

abstract class ObjRepositoryBase<O : Obj>(
	intfClass: Class<out Obj>,
	aggregateTypeId: String,
) : AggregateRepositoryBase<O>(intfClass, aggregateTypeId),
	ObjRepository<O> {

	abstract val sessionContext: SessionContext

	override fun registerParts() {
		this.addPart(ObjPartTransition::class.java, ::ObjPartTransitionImpl)
	}

	override fun doAfterCreate(aggregate: O) {
		super.doAfterCreate(aggregate)
		(aggregate as AggregateSPI).doAfterCreate(sessionContext)
	}

	override fun doBeforeStore(aggregate: O) {
		super.doBeforeStore(aggregate)
		(aggregate as AggregateSPI).doBeforeStore(sessionContext)
	}

	override fun close(obj: O) {
		obj.delete(sessionContext.userId, sessionContext.timestamp)
		store(obj)
	}

	override fun doLogChange(property: String): Boolean {
		if (NotLoggedProperties.contains(property)) {
			return false
		}
		return super.doLogChange(property)
	}

	companion object {

		private val NotLoggedProperties = setOf("closedByUser", "closedAt", "transitionList")
	}

}
