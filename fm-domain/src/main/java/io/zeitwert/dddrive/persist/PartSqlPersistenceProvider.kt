package io.zeitwert.dddrive.persist

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Entity
import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.PartListProperty
import org.jooq.DSLContext

interface PartSqlPersistenceProvider<A : Aggregate, P : Part<A>> {

	val dslContext: DSLContext

	val aggregate: Aggregate

	/**
	 * Load all parts within a load sequence.
	 */
	fun doLoadParts(block: PartSqlPersistenceProvider<A, P>.() -> Unit) {
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
		partList: PartListProperty<A, P>,
		partListTypeId: String,
	)

	/**
	 * Load the parts into a given part-list.
	 */
	@Suppress("UNCHECKED_CAST")
	fun loadPartList(
		entity: Entity<*>,
		partListId: String,
		partListTypeId: String,
	) {
		loadPartList(
			partList = (entity as EntityWithProperties).getProperty(partListId, Any::class) as PartListProperty<A, P>,
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
	fun doStoreParts(block: PartSqlPersistenceProvider<A, P>.() -> Unit) {
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
		partList: PartListProperty<A, P>,
		partListTypeId: String,
	)

	/**
	 * Add the parts from a part-list.
	 */
	@Suppress("UNCHECKED_CAST")
	fun storePartList(
		entity: Entity<*>,
		partListId: String,
		partListTypeId: String,
	) {
		storePartList(
			partList = (entity as EntityWithProperties).getProperty(partListId, Any::class) as PartListProperty<A, P>,
			partListTypeId = partListTypeId,
		)
	}

	/**
	 * Store all the parts (and close the store sequence).
	 */
	fun endStore()

}
