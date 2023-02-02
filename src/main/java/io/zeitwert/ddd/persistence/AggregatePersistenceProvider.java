package io.zeitwert.ddd.persistence;

import io.zeitwert.ddd.aggregate.model.Aggregate;

public interface AggregatePersistenceProvider<A extends Aggregate> extends PropertyProvider {

	/**
	 * Create a new Aggregate instance (purely technical)
	 * 
	 * @return new Aggregate
	 */
	A doCreate();

	/**
	 * Initialise the database records of an Aggregate with basic fields (id,
	 * tenantId) after creation (internal, technical callback).
	 *
	 * @param id       aggregate id
	 * @param tenantId tenant id
	 */
	void doInit(A aggregate, Integer id, Integer tenantId);

	/**
	 * Load core aggregate data from database and instantiate a new Aggregate. This
	 * must not load Parts, they will be loaded by @see AggregateSPI.doGet
	 * 
	 * @param id aggregate id
	 * @return instantiated Aggregate
	 */
	A doLoad(Integer id);

	/**
	 * Store the database record(s) (of the Aggregate only). The Parts will be
	 * stored from the repository.
	 * 
	 * @param aggregate aggregate to store
	 */
	void doStore(A aggregate);

}
