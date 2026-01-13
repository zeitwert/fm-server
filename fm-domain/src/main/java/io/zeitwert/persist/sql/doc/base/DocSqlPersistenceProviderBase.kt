package io.zeitwert.persist.sql.doc.base

import dddrive.app.doc.model.Doc
import dddrive.query.QuerySpec
import io.zeitwert.persist.sql.ddd.base.AggregateSqlPersistenceProviderBase
import io.zeitwert.persist.sql.ddd.util.SqlUtils
import io.zeitwert.persist.sql.doc.impl.DocPartTransitionSqlPersistenceProviderImpl

abstract class DocSqlPersistenceProviderBase<D : Doc>(
	intfClass: Class<D>,
) : AggregateSqlPersistenceProviderBase<D>(intfClass) {

	override val hasAccount = true

	override val sqlUtils = SqlUtils()

	override fun find(query: QuerySpec?): List<Any> {
		val querySpec = queryWithFilter(query)
		return doFind(querySpec)
	}

	override fun loadParts(aggregate: D) {
		DocPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			loadPartList(aggregate, "transitionList", "doc.transitionList")
		}
	}

	override fun storeParts(aggregate: D) {
		DocPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			storePartList(aggregate, "transitionList", "doc.transitionList")
		}
	}

}
