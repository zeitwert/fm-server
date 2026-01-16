package io.zeitwert.persist.mem.base

import dddrive.app.obj.model.Obj
import dddrive.db.MemoryDb
import dddrive.query.ComparisonOperator
import dddrive.query.FilterSpec
import dddrive.query.QuerySpec

/**
 * Base class for memory-based persistence providers for Obj aggregates.
 *
 * Extends AggregateMemPersistenceProviderBase and adds Obj-specific query tightening:
 * - Applies tenant and account filters via queryWithFilter
 * - Adds isClosed filter (closed_at == null) if not already present
 */
abstract class ObjMemPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : AggregateMemPersistenceProviderBase<O>(intfClass) {

	override val hasAccount = true

	override fun find(query: QuerySpec?): List<Any> {
		var querySpec = queryWithFilter(query)

		// Add isClosed filter if not already present
		if (!hasFilterFor(querySpec, "isClosed")) {
			val filters = querySpec.filters.toMutableList()
			filters.add(FilterSpec.Comparison("closedAt", ComparisonOperator.EQ, null))
			querySpec = querySpec.copy(filters = filters)
		}
		return MemoryDb
			.find(intfClass, querySpec)
			.mapNotNull { map -> map["id"] as? Int }
	}

	private fun hasFilterFor(
		querySpec: QuerySpec,
		fieldName: String,
	): Boolean =
		querySpec.filters.any { filter ->
			when (filter) {
				is FilterSpec.Comparison -> filter.path == fieldName || filter.path == "closedAt"
				else -> false
			}
		}

}
