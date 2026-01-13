package dddrive.ddd.model

import dddrive.hex.OutgoingPort
import dddrive.query.QuerySpec

interface AggregatePersistenceProvider<A : Aggregate> : OutgoingPort {

	/**
	 * Is the given Any a valid id
	 */
	fun isValidId(id: Any): Boolean

	/**
	 * Convert id to string
	 */
	fun idToString(id: Any): String

	/**
	 * Convert string to id
	 */
	fun idFromString(id: String): Any

	/**
	 * Generate new aggregate id.
	 *
	 * @return new aggregate id
	 */
	fun nextAggregateId(): Any

	/**
	 * Generate new part id.
	 *
	 * @return new part id
	 */
	fun <P : Part<A>> nextPartId(
		aggregate: A,
		partClass: Class<P>,
	): Int

	/**
	 * Load the aggregate from persistence store.
	 * Actual storage strategy depends on persistence provider.
	 *
	 * @param aggregate newly created, yet empty aggregate
	 * @param id        aggregate id
	 */
	fun load(
		aggregate: A,
		id: Any,
	)

	/**
	 * Store the aggregate on the persistence layer.
	 *
	 * @param aggregate aggregate to store
	 */
	fun store(aggregate: A)

	fun find(query: QuerySpec?): List<Any>

}
