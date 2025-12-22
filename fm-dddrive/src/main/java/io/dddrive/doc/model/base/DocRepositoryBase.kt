package io.dddrive.doc.model.base

import io.dddrive.ddd.model.AggregateRepository
import io.dddrive.ddd.model.base.AggregateRepositoryBase
import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocPartTransition
import io.dddrive.doc.model.DocRepository

abstract class DocRepositoryBase<D : Doc>(
	repoIntfClass: Class<out AggregateRepository<D>>,
	intfClass: Class<out Doc>,
	baseClass: Class<out Doc>,
	aggregateTypeId: String,
) : AggregateRepositoryBase<D>(repoIntfClass, intfClass, baseClass, aggregateTypeId),
	DocRepository<D> {

	override fun doLogChange(property: String): Boolean {
		if (NotLoggedProperties.contains(property)) {
			return false
		}
		return super.doLogChange(property)
	}

	override fun registerParts() {
		this.addPart<Doc>(Doc::class.java, DocPartTransition::class.java, DocPartTransitionBase::class.java)
	}

	companion object {

		private val NotLoggedProperties = mutableSetOf<String?>("caseDef", "isInWork", "transitionList")
	}

}
