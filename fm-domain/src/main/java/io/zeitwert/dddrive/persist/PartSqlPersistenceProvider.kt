package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.PartListProperty
import org.jooq.DSLContext

interface PartSqlPersistenceProvider<P : Part<*>> {

	val dslContext: DSLContext

	val aggregate: Aggregate

	/**
	 * Begin a load sequence.
	 * This actually loads all the parts for the given aggregate from the database.
	 *
	 * @param aggregate          aggregate
	 */
	fun beginLoad()

	/**
	 * Load the parts into a given part-list.
	 */
	fun loadParts(
		partList: PartListProperty<P>,
		partListTypeId: String,
	)

	/**
	 * Close the load sequence (free data structures).
	 */
	fun endLoad()

	/**
	 * Begin a store sequence.
	 */
	fun beginStore()

	/**
	 * Add the parts from a part-list.
	 */
	fun addParts(
		partList: PartListProperty<P>,
		partListTypeId: String,
	)

	/**
	 * Store all the parts (and close the store sequence).
	 */
	fun endStore()

}
