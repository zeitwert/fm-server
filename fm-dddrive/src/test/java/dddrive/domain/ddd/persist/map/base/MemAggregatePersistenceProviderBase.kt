package dddrive.domain.ddd.persist.map.base

import dddrive.db.MemoryDb
import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part
import dddrive.ddd.model.base.AggregatePersistenceProviderBase
import dddrive.domain.ddd.persist.map.impl.fromMap
import dddrive.domain.ddd.persist.map.impl.toMap
import dddrive.property.model.EntityWithProperties
import dddrive.query.QuerySpec
import java.util.concurrent.atomic.AtomicReference

/**
 * Base class for map-based persistence providers.
 *
 * Unlike the PTO-based MemAggregatePersistenceProviderBase, this implementation
 * automatically serializes/deserializes aggregates using the property system,
 * storing them as nested Map<String, Any?> structures.
 *
 * Storage is delegated to [MemoryDb] singleton.
 */
abstract class MemAggregatePersistenceProviderBase<A : Aggregate>(
	protected val intfClass: Class<A>,
) : AggregatePersistenceProviderBase<A>(intfClass) {

	companion object {

		private val lastId = AtomicReference(0)
		private val lastPartId = AtomicReference(0)
	}

	override fun isValidId(id: Any): Boolean = id is Int

	override fun idToString(id: Any): String = id.toString()

	override fun idFromString(id: String): Any = id.toInt()

	override fun nextAggregateId(): Any = lastId.getAndSet(lastId.get() + 1) + 1

	override fun <P : Part<A>> nextPartId(
		aggregate: A,
		partClass: Class<P>,
	): Int = lastPartId.getAndSet(lastPartId.get() + 1) + 1

	protected fun checkVersion(
		id: Int,
		version: Int,
	): Int {
		val currentVersion = (MemoryDb.get(intfClass, id)?.get("version") as? Int) ?: 0
		check(version == currentVersion + 1) { "correct version" }
		return currentVersion + 1
	}

	final override fun load(
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

	override fun transaction(work: () -> Unit) = work()

	final override fun store(aggregate: A) {
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
	 * Delegates filtering to [MemoryDb] and returns matching IDs.
	 */
	override fun find(query: QuerySpec?): List<Any> =
		MemoryDb
			.find(intfClass, query)
			.mapNotNull { map -> map["id"] as? Int }
}
