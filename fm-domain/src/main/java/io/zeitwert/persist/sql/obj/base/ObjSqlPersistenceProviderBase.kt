package io.zeitwert.persist.sql.obj.base

import dddrive.app.obj.model.Obj
import dddrive.query.ComparisonOperator
import dddrive.query.FilterSpec
import dddrive.query.QuerySpec
import io.zeitwert.app.obj.model.db.Tables
import io.zeitwert.persist.sql.ddd.base.AggregateSqlPersistenceProviderBase
import io.zeitwert.persist.sql.ddd.util.SqlUtils
import io.zeitwert.persist.sql.obj.impl.ObjPartTransitionSqlPersistenceProviderImpl
import org.slf4j.LoggerFactory

abstract class ObjSqlPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : AggregateSqlPersistenceProviderBase<O>(intfClass) {

	companion object {

		val logger = LoggerFactory.getLogger(ObjSqlPersistenceProviderBase::class.java)!!
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

	private fun hasFilterFor(
        querySpec: QuerySpec,
        fieldName: String,
	): Boolean =
		querySpec.filters.any { filter ->
			when (filter) {
				is FilterSpec.Comparison -> filter.path == fieldName || filter.path == "is_closed"
				else -> false
			}
		}

	override fun loadParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			loadPartList(aggregate, "transitionList", "obj.transitionList")
		}
	}

	override fun storeParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			storePartList(aggregate, "transitionList", "obj.transitionList")
		}
	}

}
