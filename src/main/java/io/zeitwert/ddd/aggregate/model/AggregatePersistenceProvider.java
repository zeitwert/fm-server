package io.zeitwert.ddd.aggregate.model;

import java.util.List;

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

	/**
	 * Store the search texts and tokens.
	 *
	 * @param aggregate aggregate to store
	 * @param texts     list of texts to be stored
	 * @param tokens    list of tokens to be stored
	 */
	void doStoreSearch(Aggregate aggregate, List<String> texts, List<String> tokens);

}
