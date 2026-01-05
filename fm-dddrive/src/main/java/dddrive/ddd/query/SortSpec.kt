package dddrive.ddd.query

/**
 * Specification for sorting query results.
 *
 * @property path The field path to sort by (e.g., "name", "createdAt")
 * @property direction The sort direction (ASC or DESC)
 */
data class SortSpec(
	val path: String,
	val direction: SortDirection = SortDirection.ASC,
)

