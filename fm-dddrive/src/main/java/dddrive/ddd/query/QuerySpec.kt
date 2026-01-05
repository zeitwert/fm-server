package dddrive.ddd.query

/**
 * Specification for querying aggregates.
 *
 * This is the main query object that combines filters, sorting, and pagination.
 * Use the DSL functions in [QueryDsl] for convenient construction.
 *
 * @property filters List of filter specifications (combined with AND)
 * @property sort List of sort specifications (applied in order)
 * @property offset Number of results to skip (for pagination)
 * @property limit Maximum number of results to return
 */
data class QuerySpec(
	val filters: List<FilterSpec> = emptyList(),
	val sort: List<SortSpec> = emptyList(),
	val offset: Long? = null,
	val limit: Long? = null,
) {

	companion object {

		/**
		 * An empty query specification with no filters, sorting, or pagination.
		 */
		val EMPTY = QuerySpec()

	}

}

