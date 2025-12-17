package io.dddrive.core.ddd.model

import java.time.OffsetDateTime

/**
 * This class defines the internal callbacks for an AggregateRepository
 * implementation.
 */
interface AggregateRepositorySPI<A : Aggregate> {

	/**
	 * Register the parts of the aggregate
	 */
	fun registerParts()

	/**
	 * Get the PersistenceProvider for this repository
	 *
	 * @return AggregatePersistenceProvider
	 */
	val persistenceProvider: AggregatePersistenceProvider<A>

	/**
	 * Do some work after create, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	fun doAfterCreate(
		aggregate: A,
		userId: Any,
		timestamp: OffsetDateTime,
	)

	/**
	 * Do some work after load, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	fun doAfterLoad(aggregate: A)

	/**
	 * Do some work before store, f.ex. make sure certain structures are in place
	 *
	 * @param aggregate aggregate
	 */
	fun doBeforeStore(
		aggregate: A,
		userId: Any,
		timestamp: OffsetDateTime,
	)

	/**
	 * Do some work after store, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	fun doAfterStore(aggregate: A)

}
