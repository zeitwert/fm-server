package io.zeitwert.persist.mem.base

import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.db.MemoryDb
import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part
import dddrive.ddd.model.base.AggregatePersistenceProviderBase
import dddrive.property.model.EntityWithProperties
import dddrive.query.ComparisonOperator
import dddrive.query.FilterSpec
import dddrive.query.QuerySpec
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
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

	abstract val sessionContext: SessionContext

	abstract val kernelContext: KernelContext

	abstract val hasAccount: Boolean

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

	final override fun store(aggregate: A) {
		require(intfClass != Obj::class.java) { "cannot store object" }
		require(intfClass != Doc::class.java) { "cannot store doc" }
		val map = fromAggregate(aggregate)
		MemoryDb.store(intfClass, map)
		if (Obj::class.java.isAssignableFrom(intfClass)) {
			MemoryDb.store(Obj::class.java, map)
		} else if (Doc::class.java.isAssignableFrom(intfClass)) {
			MemoryDb.store(Doc::class.java, map)
		} else {
			throw IllegalArgumentException("must be either obj or dod")
		}
	}

	protected open fun toAggregate(
		map: Map<String, Any?>,
		aggregate: A,
	) {
		(aggregate as EntityWithProperties).fromMap(map)
	}

	protected open fun fromAggregate(aggregate: A): Map<String, Any?> = (aggregate as EntityWithProperties).toMap()

	/**
	 * Add tenant and account filters to the query specification.
	 * Returns a new QuerySpec with the added filters.
	 */
	protected fun queryWithFilter(querySpec: QuerySpec?): QuerySpec {
		val filters = mutableListOf<FilterSpec>()

		// Add existing filters
		querySpec?.filters?.let { filters.addAll(it) }

		// Add tenant filter
		val tenantId = sessionContext.tenantId as Int
		if (!kernelContext.isKernelTenant(tenantId)) { // in kernel tenant everything is visible
			filters.add(FilterSpec.Comparison("tenantId", ComparisonOperator.EQ, tenantId))
		}

		// Add account filter
		if (hasAccount && sessionContext.hasAccount()) {
			val accountId = sessionContext.accountId
			filters.add(FilterSpec.Comparison("accountId", ComparisonOperator.EQ, accountId))
		}

		return QuerySpec(
			filters = filters,
			sort = querySpec?.sort ?: emptyList(),
			offset = querySpec?.offset,
			limit = querySpec?.limit,
		)
	}

}
