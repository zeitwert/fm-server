package dddrive.ddd.model

import dddrive.ddd.model.enums.CodeAggregateType
import dddrive.query.QuerySpec

/**
 * A DDD Aggregate Repository
 */
interface AggregateRepository<A : Aggregate> {

	/**
	 * Aggregate class
	 */
	val intfClass: Class<out Aggregate>

	/**
	 * Aggregate type
	 */
	val aggregateType: CodeAggregateType

	/**
	 * Repository directory
	 */
	val directory: RepositoryDirectory

	/**
	 * Convert id to string
	 */
	fun idToString(id: Any?): String?

	/**
	 * Convert string to id
	 */
	fun idFromString(id: String?): Any?

	/**
	 * Do we fire property change events for this property?
	 */
	fun doLogChange(property: String): Boolean

	/**
	 * Create a new Aggregate instance
	 */
	fun create(): A

	/**
	 * Get a read-only Aggregate with given id
	 *
	 * @return aggregate, it will be frozen
	 * @throws java.lang.RuntimeException exception when aggregate not found
	 */
	fun get(id: Any): A

	/**
	 * Get a writeable Aggregate with given id
	 *
	 * @return aggregate (not frozen)
	 * @throws java.lang.RuntimeException exception when aggregate not found
	 */
	fun load(id: Any): A

	fun transaction(
		work: () -> Unit
	)

	/**
	 * Store the Aggregate
	 */
	fun store(aggregate: A)

	/**
	 * Find aggregates matching the query specification
	 *
	 * @param query the query specification with filters, sorting, etc.
	 * @return list of aggregate IDs matching the query
	 */
	fun find(query: QuerySpec?): List<Any>

}
