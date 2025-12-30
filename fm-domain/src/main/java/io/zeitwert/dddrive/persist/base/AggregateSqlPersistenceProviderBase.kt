package io.zeitwert.dddrive.persist.base

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.base.AggregatePersistenceProviderBase
import dddrive.ddd.path.setValueByPath
import io.zeitwert.dddrive.persist.AggregateSqlPersistenceProvider
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
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
	override fun doLoad(
		aggregate: A,
		id: Any,
	) {
		require(isValidId(id)) { "valid id" }
		dslContext.transaction { _ ->
			aggregate.meta.disableCalc()
			try {
				aggregate.setValueByPath("id", id)
				baseRecordMapper.loadRecord(aggregate)
				extnRecordMapper?.loadRecord(aggregate)
				doLoadParts(aggregate)
			} finally {
				aggregate.meta.enableCalc()
			}
			aggregate.meta.calcVolatile()
		}
	}

	protected open fun doLoadParts(aggregate: A) {
	}

	@Suppress("UNCHECKED_CAST")
	override fun doStore(aggregate: A) {
		dslContext.transaction { _ ->
			baseRecordMapper.storeRecord(aggregate)
			extnRecordMapper?.storeRecord(aggregate)
			doStoreParts(aggregate)
		}
	}

	protected open fun doStoreParts(aggregate: A) {
	}

	final override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any> {
		var foreignKeys = baseRecordMapper.getIdsByForeignKey(aggregateTypeId, fkName, targetId)
		if (foreignKeys == null && extnRecordMapper != null) {
			foreignKeys = extnRecordMapper!!.getIdsByForeignKey(aggregateTypeId, fkName, targetId)
		}
		assert(foreignKeys != null) { "valid foreign key: $fkName" }
		return foreignKeys!!
	}

}
