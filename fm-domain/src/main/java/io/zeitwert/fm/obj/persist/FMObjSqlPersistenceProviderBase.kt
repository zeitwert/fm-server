package io.zeitwert.fm.obj.persist

import dddrive.app.obj.model.Obj
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.PathSpec
import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase
import io.zeitwert.dddrive.persist.util.CrnkUtils
import io.zeitwert.dddrive.persist.util.SqlUtils
import io.zeitwert.fm.obj.model.db.Tables
import org.slf4j.LoggerFactory

abstract class FMObjSqlPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : AggregateSqlPersistenceProviderBase<O>(intfClass) {

	companion object {

		val logger = LoggerFactory.getLogger(FMObjSqlPersistenceProviderBase::class.java)!!
	}

	override val hasAccount = true

	override val sqlUtils = SqlUtils()

	override fun find(query: QuerySpec?): List<Any> {
		logger.debug("find({})", query)
		val querySpec = queryWithFilter(query)
		logger.trace("find.1: {}", querySpec)
		if (!CrnkUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(Tables.OBJ.CLOSED_AT.name).filter(FilterOperator.EQ, null))
			logger.trace("find.2: {}", querySpec)
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
