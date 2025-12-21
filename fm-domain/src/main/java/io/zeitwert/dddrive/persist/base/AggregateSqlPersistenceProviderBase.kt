package io.zeitwert.dddrive.persist.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.base.AggregatePersistenceProviderBase
import io.dddrive.path.setValueByPath
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
) : AggregatePersistenceProviderBase<A>(intfClass) {

	abstract val dslContext: DSLContext

	abstract val idProvider: SqlIdProvider

	abstract val baseRecordMapper: SqlRecordMapper<Aggregate>

	abstract val extnRecordMapper: SqlRecordMapper<A>

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
		aggregate.setValueByPath("id", id)
		dslContext.transaction { _ ->
			aggregate.meta.disableCalc()
			try {
				baseRecordMapper.loadRecord(aggregate)
				extnRecordMapper.loadRecord(aggregate)
			} finally {
				aggregate.meta.enableCalc()
			}
			doLoadParts(aggregate)
			aggregate.meta.calcVolatile()
		}
	}

	protected open fun doLoadParts(aggregate: A) {
	}

	@Suppress("UNCHECKED_CAST")
	override fun doStore(aggregate: A) {
		dslContext.transaction { _ ->
			baseRecordMapper.storeRecord(aggregate)
			extnRecordMapper.storeRecord(aggregate)
			doStoreParts(aggregate)
		}
	}

	protected open fun doStoreParts(aggregate: A) {
	}

	override fun getAll(tenantId: Any): List<Any> = extnRecordMapper.getAll(tenantId)

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> {
		var foreignKeys = baseRecordMapper.getByForeignKey("", fkName, targetId)
		if (foreignKeys == null) {
			foreignKeys = extnRecordMapper.getByForeignKey("", fkName, targetId)
		}
		assert(foreignKeys != null) { "valid foreign key: $fkName" }
		return foreignKeys!!
	}

}
