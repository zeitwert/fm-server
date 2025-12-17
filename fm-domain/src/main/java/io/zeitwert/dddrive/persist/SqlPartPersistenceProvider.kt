package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.enums.CodePartListType

interface SqlPartPersistenceProvider<A : Aggregate, P : Part<A>> {

	/**
	 * Begin a load sequence.
	 * This actually loads all the parts for the given aggregateId from the database.
	 *
	 * @param aggregateId        aggregate id
	 */
	fun beginLoad(aggregateId: Any)

	/**
	 * Get the parts for a given aggregate-level part-list.
	 */
	fun getParts(
		aggregateId: Any,
		partListType: CodePartListType,
	): List<P>

	/**
	 * Get the parts of a given part-level part-list.
	 */
	fun getParts(
		aggregateId: Any,
		parentPartId: Int,
		partListType: CodePartListType,
	): List<P>

	/**
	 * Close the load sequence (free data structures).
	 */
	fun endLoad(aggregateId: Any)

	/**
	 * Begin a store sequence.
	 */
	fun beginStore(aggregateId: Any)

	/**
	 * Add a part to the aggregates list.
	 */
	fun addPart(
		aggregateId: Any,
		part: P,
	)

	/**
	 * Store all the parts and close the store sequence (free data structures).
	 */
	fun endStore(aggregateId: Any)

}
