package io.zeitwert.fm.doc.persist

import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.DocPartTransition
import io.dddrive.core.property.model.PartListProperty
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase

abstract class FMDocSqlPersistenceProviderBase<D : Doc>(
	intfClass: Class<D>,
) : AggregateSqlPersistenceProviderBase<D>(intfClass) {

	override fun doLoadParts(aggregate: D) {
		DocPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginLoad()
			loadParts(
				aggregate.getProperty(
					"transitionList",
					DocPartTransition::class,
				) as PartListProperty<DocPartTransition>,
				"doc.transitionList",
			)
			endLoad()
		}
	}

	override fun doStoreParts(aggregate: D) {
		DocPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginStore()
			addParts(
				aggregate.getProperty("transitionList", DocPartTransition::class) as PartListProperty<DocPartTransition>,
				"doc.transitionList",
			)
			endStore()
		}
	}

}
