package io.dddrive.ddd.model

import io.dddrive.property.model.EntityWithPropertiesSPI
import java.time.OffsetDateTime

/**
 * This interface defines the internal callbacks for an Aggregate
 * implementation.
 */
interface AggregateSPI : EntityWithPropertiesSPI {

	/**
	 * Generate new part id.
	 *
	 * @param partClass part class
	 * @return new part id
	 */
	fun <P : Part<*>> nextPartId(partClass: Class<P>): Int

	/**
	 * Initialise aggregate with some basic fields after creation.
	 *
	 * @param aggregateId aggregate id
	 * @param tenantId    tenant id
	 * @param userId      user id
	 * @param timestamp   timestamp
	 */
	fun doCreate(
		aggregateId: Any,
		tenantId: Any,
	)

	/**
	 * Do some work after create, f.ex. fire events, add transition etc.
	 */
	fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	)

	/**
	 * Do some work after load.
	 */
	fun doAfterLoad()

	/**
	 * Prepare for storage, f.ex. assign seqNr to parts.
	 */
	fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	)

	/**
	 * Do some work after store.
	 */
	fun doAfterStore()

}
