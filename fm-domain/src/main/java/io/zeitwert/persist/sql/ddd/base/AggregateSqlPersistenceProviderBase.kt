package io.zeitwert.persist.sql.ddd.base

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part
import dddrive.ddd.model.base.AggregatePersistenceProviderBase
import dddrive.property.path.setValueByPath
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.persist.sql.ddd.AggregateSqlPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import org.jooq.DSLContext

/**
 * Base class for jOOQ-based persistence providers implementing the AggregatePersistenceProvider interface.
 *
 * This class provides the foundation for persisting aggregates to PostgreSQL using jOOQ.
 * Concrete implementations should handle specific aggregate types (Obj, Doc) with their
 * respective table structures (base and extension tables).
 */
abstract class AggregateSqlPersistenceProviderBase<A : Aggregate>(
	intfClass: Class<A>,
) : AggregatePersistenceProviderBase<A>(intfClass),
	AggregateSqlPersistenceProvider<A>,
	AggregateFindMixin {

	abstract override val dslContext: DSLContext

	abstract override val kernelContext: KernelContext

	abstract val idProvider: SqlIdProvider

	abstract val baseRecordMapper: SqlRecordMapper<Aggregate>

	abstract val extnRecordMapper: SqlRecordMapper<A>?

	override fun isValidId(id: Any): Boolean = id is Int

	override fun idToString(id: Any): String = id.toString()

	override fun idFromString(id: String): Any = id.toInt()

	override fun nextAggregateId(): Any = idProvider.nextAggregateId()

	@Suppress("UNCHECKED_CAST")
	override fun <P : Part<A>> nextPartId(
		aggregate: A,
		partClass: Class<P>,
	): Int = idProvider.nextPartId(aggregate, partClass)

	@Suppress("UNCHECKED_CAST")
	override fun load(
		aggregate: A,
		id: Any,
	) {
		require(isValidId(id)) { "valid id" }
		aggregate.meta.disableCalc()
		try {
			aggregate.setValueByPath("id", id)
			baseRecordMapper.loadRecord(aggregate)
			extnRecordMapper?.loadRecord(aggregate)
			loadParts(aggregate)
		} finally {
			aggregate.meta.enableCalc()
		}
		aggregate.meta.calcVolatile()
	}

	protected open fun loadParts(aggregate: A) {
	}

	override fun transaction(work: () -> Unit) {
		dslContext.transaction { _ -> work() }
	}

	@Suppress("UNCHECKED_CAST")
	override fun store(aggregate: A) {
		baseRecordMapper.storeRecord(aggregate)
		extnRecordMapper?.storeRecord(aggregate)
		storeParts(aggregate)
	}

	protected open fun storeParts(aggregate: A) {
	}

}
