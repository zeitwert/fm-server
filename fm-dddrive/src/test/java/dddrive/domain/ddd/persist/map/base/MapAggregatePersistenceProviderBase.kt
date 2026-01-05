package dddrive.domain.ddd.persist.map.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.base.AggregatePersistenceProviderBase
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.domain.ddd.persist.map.impl.fromMap
import dddrive.domain.ddd.persist.map.impl.toMap
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.QuerySpec
import java.util.concurrent.atomic.AtomicReference

/**
 * Base class for map-based persistence providers.
 *
 * Unlike the PTO-based MemAggregatePersistenceProviderBase, this implementation
 * automatically serializes/deserializes aggregates using the property system,
 * storing them as nested Map<String, Any?> structures.
 */
abstract class MapAggregatePersistenceProviderBase<A : Aggregate>(
	intfClass: Class<A>,
) : AggregatePersistenceProviderBase<A>(intfClass) {

	companion object {

		private val lastId = AtomicReference(0)
		private val lastPartId = AtomicReference(0)
	}

	protected val aggregates: MutableMap<Int, Map<String, Any?>> = HashMap()

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
		val currentVersion = (aggregates[id]?.get("version") as? Int) ?: 0
		check(version == currentVersion + 1) { "correct version" }
		return currentVersion + 1
	}

	final override fun doLoad(
		aggregate: A,
		id: Any,
	) {
		require(isValidId(id)) { "valid id" }
		val map = aggregates[id as Int]
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
		val id = map["id"] as? Int
		id?.let { aggregates[it] = map }
	}

	/**
	 * Find aggregates matching the query specification.
	 * Supports basic EQ filters on map properties.
	 */
	fun find(query: QuerySpec?): List<Any> {
		if (query == null) {
			return aggregates.values.mapNotNull { map -> map["id"] as? Int }.toList()
		}

		return aggregates.values
			.filter { map ->
				query.filters.all { filter ->
					val path = filter.path.toString()
					val mapKey = if (path.endsWith("Id")) path else "${path}Id"
					val mapValue = map[path] ?: map[mapKey]
					when (filter.operator) {
						FilterOperator.EQ -> mapValue == filter.getValue()
						else -> true // Ignore unsupported operators in test implementation
					}
				}
			}
			.mapNotNull { map -> map["id"] as? Int }
			.toList()
	}
}
