package dddrive.app.doc.model.base

import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext
import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.impl.DocPartTransitionImpl
import dddrive.ddd.model.base.AggregateRepositoryBase

abstract class DocRepositoryBase<D : Doc>(
	intfClass: Class<out Doc>,
	aggregateTypeId: String,
) : AggregateRepositoryBase<D>(intfClass, aggregateTypeId),
	DocRepository<D> {

	abstract val sessionContext: SessionContext

	override fun registerParts() {
		this.addPart(DocPartTransition::class.java, ::DocPartTransitionImpl)
	}

	override fun doAfterCreate(aggregate: D) {
		super.doAfterCreate(aggregate)
		(aggregate as AggregateSPI).doAfterCreate(sessionContext)
	}

	override fun doBeforeStore(aggregate: D) {
		super.doBeforeStore(aggregate)
		(aggregate as AggregateSPI).doBeforeStore(sessionContext)
	}

	override fun doLogChange(property: String): Boolean {
		if (NotLoggedProperties.contains(property)) {
			return false
		}
		return super.doLogChange(property)
	}

	companion object {

		private val NotLoggedProperties = setOf("caseDef", "isInWork", "transitionList")
	}

}
