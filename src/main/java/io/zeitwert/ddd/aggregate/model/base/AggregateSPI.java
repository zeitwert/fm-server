package io.zeitwert.ddd.aggregate.model.base;

import io.zeitwert.ddd.property.model.PropertyProvider;

/**
 * This interface defines the internal callbacks for an Aggregate
 * implementation.
 */
public interface AggregateSPI {

	/**
	 * Get the aggregates property provider.
	 * 
	 * @return property provider
	 */
	PropertyProvider getPropertyProvider();

	/**
	 * Initialise the database records of an Aggregate with some basic fields (id,
	 * tenantId) after creation (internal, technical callback).
	 *
	 * @param aggregateId aggregate id
	 * @param tenantId    tenant id
	 */
	void doInit(Integer aggregateId, Integer tenantId);

	/**
	 * Do some work after create, f.ex. fire events, add transition etc.
	 */
	void doAfterCreate();

	/**
	 * Assign Parts to Aggregate lists after Load
	 */
	void doAssignParts();

	/**
	 * Do some work after load.
	 */
	void doAfterLoad();

	/**
	 * Prepare for storage, f.ex. assign seqNr to parts.
	 */
	void doBeforeStore();

	/**
	 * Store the database record(s) (of the Aggregate only). The Parts will be
	 * stored from the repository.
	 */
	void doStore();

	/**
	 * Calculate the search text and token strings (add via addSearchToken,
	 * addSearchText).
	 */
	void doCalcSearch();

	/**
	 * Do some work after store.
	 */
	void doAfterStore();

}
