package io.dddrive.core.ddd.model;

import java.time.OffsetDateTime;

/**
 * This interface defines the internal callbacks for an Aggregate
 * implementation.
 */
public interface AggregateSPI {

	/**
	 * Generate new part id.
	 *
	 * @param partClass part class
	 * @return new part id
	 */
	<P extends Part<?>> int nextPartId(Class<P> partClass);

	/**
	 * Initialise aggregate with some basic fields after creation.
	 *
	 * @param aggregateId aggregate id
	 * @param tenantId    tenant id
	 * @param userId      user id
	 * @param timestamp   timestamp
	 */
	void doCreate(Object aggregateId, Object tenantId, Object userId, OffsetDateTime timestamp);

	/**
	 * Do some work after create, f.ex. fire events, add transition etc.
	 */
	void doAfterCreate(Object userId, OffsetDateTime timestamp);

	/**
	 * Do some work after load.
	 */
	void doAfterLoad();

	/**
	 * Prepare for storage, f.ex. assign seqNr to parts.
	 */
	void doBeforeStore(Object userId, OffsetDateTime timestamp);

	/**
	 * Do some work after store.
	 */
	void doAfterStore();

}
