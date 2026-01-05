package dddrive.ddd.query

import dddrive.ddd.query.SortDirection.ASC

/**
 * DSL entry point for building a [QuerySpec].
 *
 * Example usage:
 * ```kotlin
 * val query = query {
 *     filter { "tenantId" eq 42 }
 *     filter { "status" neq "closed" }
 *     sortBy("modifiedAt", DESC)
 *     limit(50)
 * }
 * ```
 */
fun query(block: QueryBuilder.() -> Unit): QuerySpec {
	return QueryBuilder().apply(block).build()
}

/**
 * Builder for constructing [QuerySpec] instances.
 */
@QueryDslMarker
class QueryBuilder {

	private val filters = mutableListOf<FilterSpec>()
	private val sortSpecs = mutableListOf<SortSpec>()
	private var offset: Long? = null
	private var limit: Long? = null

	/**
	 * Add a filter to the query.
	 *
	 * Example:
	 * ```kotlin
	 * filter { "name" eq "John" }
	 * filter { "age" ge 18 }
	 * ```
	 */
	fun filter(block: FilterBuilder.() -> FilterSpec) {
		filters.add(FilterBuilder().block())
	}

	/**
	 * Add an OR compound filter to the query.
	 *
	 * Example:
	 * ```kotlin
	 * or {
	 *     filter { "status" eq "active" }
	 *     filter { "priority" eq "high" }
	 * }
	 * ```
	 */
	fun or(block: OrFilterBuilder.() -> Unit) {
		filters.add(OrFilterBuilder().apply(block).build())
	}

	/**
	 * Add a sort specification to the query.
	 *
	 * @param path The field path to sort by
	 * @param direction The sort direction (default: ASC)
	 */
	fun sortBy(path: String, direction: SortDirection = ASC) {
		sortSpecs.add(SortSpec(path, direction))
	}

	/**
	 * Set the offset for pagination (number of results to skip).
	 */
	fun offset(value: Long) {
		offset = value
	}

	/**
	 * Set the limit for pagination (maximum number of results).
	 */
	fun limit(value: Long) {
		limit = value
	}

	/**
	 * Build the [QuerySpec] from the current builder state.
	 */
	fun build() = QuerySpec(filters.toList(), sortSpecs.toList(), offset, limit)

}

/**
 * Builder for constructing filter expressions.
 */
@QueryDslMarker
class FilterBuilder {

	/**
	 * Creates an equality filter: field = value
	 */
	infix fun String.eq(value: Any?): FilterSpec.Comparison =
		FilterSpec.Comparison(this, ComparisonOperator.EQ, value)

	/**
	 * Creates a not-equal filter: field != value
	 */
	infix fun String.neq(value: Any?): FilterSpec.Comparison =
		FilterSpec.Comparison(this, ComparisonOperator.NEQ, value)

	/**
	 * Creates a greater-than filter: field > value
	 */
	infix fun String.gt(value: Any?): FilterSpec.Comparison =
		FilterSpec.Comparison(this, ComparisonOperator.GT, value)

	/**
	 * Creates a greater-than-or-equal filter: field >= value
	 */
	infix fun String.ge(value: Any?): FilterSpec.Comparison =
		FilterSpec.Comparison(this, ComparisonOperator.GE, value)

	/**
	 * Creates a less-than filter: field < value
	 */
	infix fun String.lt(value: Any?): FilterSpec.Comparison =
		FilterSpec.Comparison(this, ComparisonOperator.LT, value)

	/**
	 * Creates a less-than-or-equal filter: field <= value
	 */
	infix fun String.le(value: Any?): FilterSpec.Comparison =
		FilterSpec.Comparison(this, ComparisonOperator.LE, value)

	/**
	 * Creates a LIKE filter for pattern matching.
	 * Use * as wildcard character (will be converted to % for SQL).
	 */
	infix fun String.like(value: String): FilterSpec.Comparison =
		FilterSpec.Comparison(this, ComparisonOperator.LIKE, value)

	/**
	 * Creates an IN filter for checking membership in a collection.
	 */
	infix fun String.inList(values: Collection<Any>): FilterSpec.In =
		FilterSpec.In(this, values)

}

/**
 * Builder for constructing OR compound filters.
 */
@QueryDslMarker
class OrFilterBuilder {

	private val filters = mutableListOf<FilterSpec>()

	/**
	 * Add a filter to the OR compound.
	 */
	fun filter(block: FilterBuilder.() -> FilterSpec) {
		filters.add(FilterBuilder().block())
	}

	/**
	 * Build the OR filter from the current builder state.
	 */
	fun build(): FilterSpec.Or = FilterSpec.Or(filters.toList())

}

/**
 * DSL marker annotation to prevent scope leakage in nested builders.
 */
@DslMarker
annotation class QueryDslMarker

