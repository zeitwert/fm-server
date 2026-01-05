package io.zeitwert.fm.obj.persist

import dddrive.app.obj.model.Obj
import dddrive.ddd.query.ComparisonOperator
import dddrive.ddd.query.FilterSpec
import dddrive.ddd.query.QuerySpec
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase
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
		var querySpec = queryWithFilter(query)
		logger.trace("find.1: {}", querySpec)

		// Add isClosed filter if not already present
		if (!hasFilterFor(querySpec, "isClosed")) {
			val filters = querySpec.filters.toMutableList()
			filters.add(FilterSpec.Comparison(Tables.OBJ.CLOSED_AT.name, ComparisonOperator.EQ, null))
			querySpec = querySpec.copy(filters = filters)
			logger.trace("find.2: {}", querySpec)
		}
		return doFind(querySpec)
	}

	private fun hasFilterFor(querySpec: QuerySpec, fieldName: String): Boolean {
		return querySpec.filters.any { filter ->
			when (filter) {
				is FilterSpec.Comparison -> filter.path == fieldName || filter.path == "is_closed"
				else -> false
			}
		}
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
