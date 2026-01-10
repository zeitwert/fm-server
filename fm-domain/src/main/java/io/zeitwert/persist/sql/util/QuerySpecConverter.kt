package io.zeitwert.persist.sql.util

import dddrive.ddd.query.ComparisonOperator
import dddrive.ddd.query.FilterSpec
import dddrive.ddd.query.QuerySpec
import dddrive.ddd.query.SortDirection
import dddrive.ddd.query.SortSpec
import io.crnk.core.queryspec.Direction
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.QuerySpec as CrnkQuerySpec

/**
 * Converter to transform crnk QuerySpec to dddrive QuerySpec.
 *
 * This allows the UI layer to continue using crnk's QuerySpec while
 * the domain layer uses the new dddrive QuerySpec internally.
 */
object QuerySpecConverter {

	/**
	 * Convert a crnk QuerySpec to a dddrive QuerySpec.
	 *
	 * @param crnkQuery The crnk QuerySpec to convert (can be null)
	 * @return The converted dddrive QuerySpec, or an empty QuerySpec if input is null
	 */
	fun convert(crnkQuery: CrnkQuerySpec?): QuerySpec {
		if (crnkQuery == null) {
			return QuerySpec.EMPTY
		}

		val filters = crnkQuery.filters.map { convertFilter(it) }
		val sort = crnkQuery.sort.map { convertSort(it) }

		return QuerySpec(
			filters = filters,
			sort = sort,
			offset = crnkQuery.offset,
			limit = crnkQuery.limit,
		)
	}

	private fun convertFilter(crnkFilter: io.crnk.core.queryspec.FilterSpec): FilterSpec {
		val path = CrnkUtils.getPath(crnkFilter)

		// Handle OR compound filter
		if (crnkFilter.operator == FilterOperator.OR && crnkFilter.expression != null) {
			return FilterSpec.Or(
				filters = crnkFilter.expression.map { convertFilter(it) },
			)
		}

		// Handle IN operator (custom or with collection value)
		if (crnkFilter.operator == CustomFilters.IN) {
			@Suppress("UNCHECKED_CAST")
			val values = crnkFilter.getValue<Collection<Any>>()
			return FilterSpec.In(path, values)
		}

		// Handle EQ with collection value (treated as IN)
		val value = crnkFilter.getValue<Any?>()
		if (crnkFilter.operator == FilterOperator.EQ && value is Collection<*>) {
			@Suppress("UNCHECKED_CAST")
			return FilterSpec.In(path, value as Collection<Any>)
		}

		// Handle standard comparison operators
		val operator = convertOperator(crnkFilter.operator)
		return FilterSpec.Comparison(path, operator, value)
	}

	private fun convertOperator(crnkOperator: FilterOperator): ComparisonOperator =
		when (crnkOperator) {
			FilterOperator.EQ -> ComparisonOperator.EQ
			FilterOperator.NEQ -> ComparisonOperator.NEQ
			FilterOperator.GT -> ComparisonOperator.GT
			FilterOperator.GE -> ComparisonOperator.GE
			FilterOperator.LT -> ComparisonOperator.LT
			FilterOperator.LE -> ComparisonOperator.LE
			FilterOperator.LIKE -> ComparisonOperator.LIKE
			else -> throw IllegalArgumentException("Unsupported filter operator: $crnkOperator")
		}

	private fun convertSort(crnkSort: io.crnk.core.queryspec.SortSpec): SortSpec =
		SortSpec(
			path = crnkSort.path.toString(),
			direction = if (crnkSort.direction == Direction.ASC) SortDirection.ASC else SortDirection.DESC,
		)

}
