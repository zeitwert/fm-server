package dddrive.app.doc.model.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPartTransition
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.impl.DocPartTransitionImpl
import dddrive.ddd.core.model.base.AggregateRepositoryBase

abstract class DocRepositoryBase<D : Doc>(
	intfClass: Class<out Doc>,
	aggregateTypeId: String,
) : dddrive.ddd.core.model.base.AggregateRepositoryBase<D>(intfClass, aggregateTypeId),
	DocRepository<D> {

	override fun doLogChange(property: String): Boolean {
		if (NotLoggedProperties.contains(property)) {
			return false
		}
		return super.doLogChange(property)
	}

	override fun registerParts() {
		this.addPart(DocPartTransition::class.java, ::DocPartTransitionImpl)
	}

	companion object {

		private val NotLoggedProperties = setOf("caseDef", "isInWork", "transitionList")
	}

}
