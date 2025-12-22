package io.zeitwert.dddrive.persist

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.Part
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.PartListProperty
import org.jooq.DSLContext

interface PartSqlPersistenceProvider<P : Part<*>> {

	val dslContext: DSLContext

	val aggregate: Aggregate

	/**
	 * Load all parts within a load sequence.
	 */
	fun doLoadParts(block: PartSqlPersistenceProvider<P>.() -> Unit) {
		beginLoad()
		return try {
			block()
		} finally {
			endLoad()
		}
	}

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
	fun loadPartList(
		partList: PartListProperty<P>,
		partListTypeId: String,
	)

	/**
	 * Load the parts into a given part-list.
	 */
	@Suppress("UNCHECKED_CAST")
	fun loadPartList(
		entity: EntityWithProperties,
		partListId: String,
		partListTypeId: String,
	) {
		loadPartList(
			partList = entity.getProperty(partListId, Any::class) as PartListProperty<P>,
			partListTypeId = partListTypeId,
		)
	}

	/**
	 * Close the load sequence (free data structures).
	 */
	fun endLoad()

	/**
	 * Store all parts within a load sequence.
	 */
	fun doStoreParts(block: PartSqlPersistenceProvider<P>.() -> Unit) {
		beginStore()
		return try {
			block()
		} finally {
			endStore()
		}
	}

	/**
	 * Begin a store sequence.
	 */
	fun beginStore()

	/**
	 * Add the parts from a part-list.
	 */
	fun storePartList(
		partList: PartListProperty<P>,
		partListTypeId: String,
	)

	/**
	 * Add the parts from a part-list.
	 */
	@Suppress("UNCHECKED_CAST")
	fun storePartList(
		entity: EntityWithProperties,
		partListId: String,
		partListTypeId: String,
	) {
		storePartList(
			partList = entity.getProperty(partListId, Any::class) as PartListProperty<P>,
			partListTypeId = partListTypeId,
		)
	}

	/**
	 * Store all the parts (and close the store sequence).
	 */
	fun endStore()

}
