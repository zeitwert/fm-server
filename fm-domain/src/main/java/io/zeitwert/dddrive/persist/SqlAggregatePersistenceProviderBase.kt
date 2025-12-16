package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.AggregateSPI
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.base.AggregatePersistenceProviderBase
import io.dddrive.core.obj.model.Obj
import io.dddrive.util.Invariant.requireThis
import org.jooq.DSLContext
import org.jooq.UpdatableRecord

/**
 * Base class for jOOQ-based persistence providers implementing the AggregatePersistenceProvider interface.
 *
 * This class provides the foundation for persisting aggregates to PostgreSQL using jOOQ.
 * Concrete implementations should handle specific aggregate types (Obj, Doc) with their
 * respective table structures.
 */
abstract class SqlAggregatePersistenceProviderBase<A : Obj, BR : UpdatableRecord<BR>, ER : UpdatableRecord<ER>>(
	intfClass: Class<A>,
) : AggregatePersistenceProviderBase<A>(intfClass) {

	abstract val baseRecordMapper: SqlAggregateRecordMapper<Obj, BR>

	abstract val extnRecordMapper: SqlAggregateRecordMapper<A, ER>

	abstract fun dslContext(): DSLContext

	override fun isValidId(id: Any): Boolean = id is Int

	override fun idToString(id: Any): String = id.toString()

	override fun idFromString(id: String): Any = id.toInt()

	override fun nextAggregateId(): Any = baseRecordMapper.nextId()

	override fun <P : Part<A>> nextPartId(
		aggregate: A,
		partClass: Class<P>,
	): Int = (aggregate as AggregateSPI).nextPartId(partClass)

	override fun doLoad(
		aggregate: A,
		id: Any,
	) {
		requireThis(isValidId(id)) { "valid id" }
		dslContext().transaction { _ ->
			val er = extnRecordMapper.loadRecord(id)
			val br = baseRecordMapper.loadRecord(id)
			aggregate.meta.disableCalc()
			try {
				baseRecordMapper.mapFromRecord(aggregate, br)
				extnRecordMapper.mapFromRecord(aggregate, er)
			} finally {
				aggregate.meta.enableCalc()
			}
			aggregate.calcVolatile()
		}
	}

	override fun doStore(aggregate: A) {
		dslContext().transaction { _ ->
			val br = baseRecordMapper.mapToRecord(aggregate)
			val er = extnRecordMapper.mapToRecord(aggregate)
			try {
				baseRecordMapper.storeRecord(br, aggregate)
				extnRecordMapper.storeRecord(er, aggregate)
				System.err.println("stored:\n$br\n$er")
			} catch (e: RuntimeException) {
				System.err.println("${e.message}:\n$br\n$er")
				throw e // Re-throw to trigger rollback
			}
		}
	}

	override fun getAll(tenantId: Any): List<Any> = extnRecordMapper.getAll(tenantId)

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> = extnRecordMapper.getByForeignKey(fkName, targetId)!!

}
