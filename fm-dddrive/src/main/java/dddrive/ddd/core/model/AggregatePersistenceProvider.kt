package dddrive.ddd.core.model

interface AggregatePersistenceProvider<A : Aggregate?> {

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
	fun doLoad(
		aggregate: A,
		id: Any,
	)

	/**
	 * Store the aggregate on the persistence layer.
	 *
	 * @param aggregate aggregate to store
	 */
	fun doStore(aggregate: A)

	/**
	 * Get a list of Aggregate Ids with the given foreign key pointing to targetId
	 */
	fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>

}
