package io.zeitwert.fm.obj.persist

import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.PathSpec
import io.crnk.core.queryspec.QuerySpec
import io.dddrive.obj.model.Obj
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase
import io.zeitwert.dddrive.persist.util.CrnkUtils
import io.zeitwert.dddrive.persist.util.SqlUtils

abstract class FMObjSqlPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : AggregateSqlPersistenceProviderBase<O>(intfClass) {

	override val hasAccount = true

	override val sqlUtils = SqlUtils()

	override fun find(query: QuerySpec?): List<Any> {
		val querySpec = queryWithFilter(query)
		if (!CrnkUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of("closed_at").filter(FilterOperator.EQ, null))
		}
		return doFind(querySpec)
	}

	override fun doLoadParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			loadPartList(aggregate, "transitionList", "obj.transitionList")
		}
	}

	override fun doStoreParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			storePartList(aggregate, "transitionList", "obj.transitionList")
		}
	}

}
