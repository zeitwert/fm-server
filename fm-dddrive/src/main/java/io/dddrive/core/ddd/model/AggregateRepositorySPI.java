package io.dddrive.core.ddd.model;

import java.time.OffsetDateTime;

/**
 * This class defines the internal callbacks for an AggregateRepository
 * implementation.
 */
public interface AggregateRepositorySPI<A extends Aggregate> {

	/**
	 * Register the parts of the aggregate
	 */
	void registerParts();

	/**
	 * Get the PersistenceProvider for this repository
	 *
	 * @return AggregatePersistenceProvider
	 */
	AggregatePersistenceProvider<A> getPersistenceProvider();

	/**
	 * Do some work after create, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	void doAfterCreate(A aggregate, Object userId, OffsetDateTime timestamp);

	/**
	 * Do some work after load, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	void doAfterLoad(A aggregate);

	/**
	 * Do some work before store, f.ex. make sure certain structures are in place
	 *
	 * @param aggregate aggregate
	 */
	void doBeforeStore(A aggregate, Object userId, OffsetDateTime timestamp);

	/**
	 * Do some work after store, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	void doAfterStore(A aggregate);

}
