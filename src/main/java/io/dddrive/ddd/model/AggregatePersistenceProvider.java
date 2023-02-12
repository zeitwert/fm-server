package io.dddrive.ddd.model;

public interface AggregatePersistenceProvider<A extends Aggregate> {

	/**
	 * The class of the entity that is managed by this provider.
	 *
	 * @return entity class
	 */
	Class<?> getEntityClass();

	/**
	 * Provide a new Aggregate id
	 *
	 * @return new aggregate id
	 */
	Integer nextAggregateId();

	/**
	 * Create a new Aggregate instance (purely technical)
	 *
	 * @return new Aggregate
	 */
	A doCreate();

	/**
	 * Load core aggregate data from database and instantiate a new Aggregate. This
	 * must not load Parts, they will be loaded by @see AggregateSPI.doGet and their
	 * corresponding repositories.
	 *
	 * @param id aggregate id
	 * @return instantiated Aggregate
	 */
	A doLoad(Integer id);

	/**
	 * Store the database record(s) (of the Aggregate only). The Parts will be
	 * stored from their corresponding repositories.
	 *
	 * @param aggregate aggregate to store
	 */
	void doStore(A aggregate);

}
