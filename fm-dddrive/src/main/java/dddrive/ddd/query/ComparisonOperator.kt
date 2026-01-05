package dddrive.ddd.query

/**
 * Comparison operators for filter specifications.
 */
enum class ComparisonOperator {
	/** Equals */
	EQ,
	/** Not equals */
	NEQ,
	/** Greater than */
	GT,
	/** Greater than or equal */
	GE,
	/** Less than */
	LT,
	/** Less than or equal */
	LE,
	/** Like (pattern matching with * as wildcard) */
	LIKE,
}

