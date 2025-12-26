package io.dddrive.doc.model.base

import io.dddrive.ddd.model.base.AggregateRepositoryBase
import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocPartTransition
import io.dddrive.doc.model.DocRepository
import io.dddrive.doc.model.impl.DocPartTransitionImpl

abstract class DocRepositoryBase<D : Doc>(
	intfClass: Class<out Doc>,
	aggregateTypeId: String,
) : AggregateRepositoryBase<D>(intfClass, aggregateTypeId),
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
