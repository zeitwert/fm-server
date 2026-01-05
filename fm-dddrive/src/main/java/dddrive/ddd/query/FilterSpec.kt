package dddrive.ddd.query

/**
 * Specification for filtering query results.
 *
 * This is a sealed class hierarchy representing different types of filters:
 * - [Comparison]: Simple comparison (eq, neq, gt, ge, lt, le, like)
 * - [In]: Membership in a collection
 * - [Or]: Disjunction of multiple filters
 */
sealed class FilterSpec {

	/**
	 * A comparison filter (e.g., field = value, field > value).
	 *
	 * @property path The field path to filter on
	 * @property operator The comparison operator
	 * @property value The value to compare against (can be null for EQ/NEQ)
	 */
	data class Comparison(
		val path: String,
		val operator: ComparisonOperator,
		val value: Any?,
	) : FilterSpec()

	/**
	 * An IN filter for checking membership in a collection.
	 *
	 * @property path The field path to filter on
	 * @property values The collection of values to check membership against
	 */
	data class In(
		val path: String,
		val values: Collection<Any>,
	) : FilterSpec()

	/**
	 * An OR filter combining multiple filters with disjunction.
	 * At least one of the contained filters must match.
	 *
	 * @property filters The list of filters to combine with OR
	 */
	data class Or(
		val filters: List<FilterSpec>,
	) : FilterSpec()

}

