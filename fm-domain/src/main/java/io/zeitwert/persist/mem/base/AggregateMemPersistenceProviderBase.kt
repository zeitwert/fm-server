package io.zeitwert.persist.mem.base

import dddrive.db.MemoryDb
import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part
import dddrive.ddd.model.base.AggregatePersistenceProviderBase
import dddrive.property.model.EntityWithProperties
import dddrive.query.QuerySpec
import java.util.concurrent.atomic.AtomicInteger

/**
 * Base class for map-based persistence providers in fm-domain.
 *
 * Uses the MemoryDb singleton from dddrive for storage, automatically
 * serializing/deserializing aggregates using the property system.
 */
abstract class AggregateMemPersistenceProviderBase<A : Aggregate>(
	protected val intfClass: Class<A>,
) : AggregatePersistenceProviderBase<A>(intfClass) {

	companion object {

		private val lastId = AtomicInteger(0)
		private val lastPartId = AtomicInteger(0)
	}

	override fun isValidId(id: Any): Boolean = id is Int

	override fun idToString(id: Any): String = id.toString()

	override fun idFromString(id: String): Any = id.toInt()

	override fun nextAggregateId(): Any = lastId.incrementAndGet()

	override fun <P : Part<A>> nextPartId(
		aggregate: A,
		partClass: Class<P>,
	): Int = lastPartId.incrementAndGet()

	protected fun checkVersion(
		id: Int,
		version: Int,
	): Int {
		val currentVersion = (MemoryDb.get(intfClass, id)?.get("version") as? Int) ?: 0
		check(version == currentVersion + 1) { "correct version" }
		return currentVersion + 1
	}

	final override fun doLoad(
		aggregate: A,
		id: Any,
	) {
		require(isValidId(id)) { "valid id" }
		val map = MemoryDb.get(intfClass, id as Int)
		check(map != null) { "aggregate found for id ($id)" }

		aggregate.meta.disableCalc()
		try {
			toAggregate(map, aggregate)
		} finally {
			aggregate.meta.enableCalc()
			aggregate.meta.calcVolatile()
		}
	}

	final override fun doStore(aggregate: A) {
		val map = fromAggregate(aggregate)
		store(map)
	}

	/**
	 * Deserializes a map into an aggregate.
	 * Override to add custom handling if needed.
	 */
	protected open fun toAggregate(
		map: Map<String, Any?>,
		aggregate: A,
	) {
		(aggregate as EntityWithProperties).fromMap(map)
	}

	/**
	 * Serializes an aggregate to a map.
	 * Override to add custom handling if needed.
	 */
	protected open fun fromAggregate(aggregate: A): Map<String, Any?> = (aggregate as EntityWithProperties).toMap()

	protected open fun store(map: Map<String, Any?>) {
		MemoryDb.store(intfClass, map)
	}

	/**
	 * Find aggregates matching the query specification.
	 * Delegates filtering to MemoryDb and returns matching IDs.
	 */
	override fun find(query: QuerySpec?): List<Any> =
		MemoryDb
			.find(intfClass, query)
			.mapNotNull { map -> map["id"] as? Int }
}
