package io.zeitwert.fm.doc.persist

import io.dddrive.doc.model.Doc
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase
import io.zeitwert.dddrive.persist.util.SqlUtils

abstract class FMDocSqlPersistenceProviderBase<D : Doc>(
	intfClass: Class<D>,
) : AggregateSqlPersistenceProviderBase<D>(intfClass) {

	override val hasAccount = true

	override val sqlUtils = SqlUtils()

	override fun doLoadParts(aggregate: D) {
		DocPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			loadPartList(aggregate, "transitionList", "doc.transitionList")
		}
	}

	override fun doStoreParts(aggregate: D) {
		DocPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			storePartList(aggregate, "transitionList", "doc.transitionList")
		}
	}

}
