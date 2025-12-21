package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.enums.CodePartListType
import org.jooq.DSLContext

interface SqlPartPersistenceProvider<P : Part<*>> {

	fun init(
		aggregateId: Any,
		dslContext: DSLContext,
	)

	/**
	 * Begin a load sequence.
	 * This actually loads all the parts for the given aggregateId from the database.
	 *
	 * @param aggregateId        aggregate id
	 */
	fun beginLoad()

	/**
	 * Get the parts for a given aggregate-level part-list.
	 */
	fun getParts(
		partListType: CodePartListType,
	): List<P>

	/**
	 * Get the parts of a given part-level part-list.
	 */
	fun getParts(
		parentPartId: Int,
		partListType: CodePartListType,
	): List<P>

	/**
	 * Close the load sequence (free data structures).
	 */
	fun endLoad()

	/**
	 * Begin a store sequence.
	 */
	fun beginStore()

	/**
	 * Add a part to the list.
	 */
	fun addPart(
		aggregateId: Any,
		part: P,
	)

	/**
	 * Store all the parts (and close the store sequence).
	 */
	fun endStore()

}
