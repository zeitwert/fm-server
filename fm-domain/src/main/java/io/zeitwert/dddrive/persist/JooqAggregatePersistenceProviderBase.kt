package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateMeta
import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.ddd.model.AggregateSPI
import io.dddrive.core.ddd.model.Part
import io.dddrive.path.setValueByPath
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import java.lang.reflect.InvocationTargetException

/**
 * Base class for jOOQ-based persistence providers implementing the AggregatePersistenceProvider interface.
 *
 * This class provides the foundation for persisting aggregates to PostgreSQL using jOOQ.
 * Concrete implementations should handle specific aggregate types (Obj, Doc) with their
 * respective table structures.
 */
abstract class JooqAggregatePersistenceProviderBase<A : Aggregate> : AggregatePersistenceProvider<A> {

	/**
	 * Returns the jOOQ DSLContext for database operations.
	 */
	abstract fun dslContext(): DSLContext

	override fun isValidId(id: Any): Boolean = id is Int

	override fun idToString(id: Any): String = id.toString()

	override fun idFromString(id: String): Any = id.toInt()

	override fun <P : Part<A>> nextPartId(
		aggregate: A,
		partClass: Class<P>,
	): Int = (aggregate as AggregateSPI).nextPartId(partClass)

	/**
	 * Loads an aggregate from the database.
	 *
	 * @param aggregate The empty aggregate instance to populate
	 * @param id The aggregate ID
	 */
	override fun doLoad(
		aggregate: A,
		id: Any,
	) {
		require(isValidId(id)) { "valid id" }
		val record = loadRecord(id as Int)
		assert(record != null) { "aggregate found for id ($id)" }
		(aggregate as AggregateMeta).disableCalc()
		try {
			toAggregate(record!!, aggregate)
		} finally {
			(aggregate as AggregateMeta).enableCalc()
			aggregate.calcVolatile()
		}
	}

	/**
	 * Loads the base record from the database.
	 *
	 * @param id The aggregate ID
	 * @return The loaded record, or null if not found
	 */
	protected abstract fun loadRecord(id: Int): UpdatableRecord<*>?

	/**
	 * Maps fields from a jOOQ record to an aggregate.
	 * Subclasses should call super.toAggregate() and then add their specific field mappings.
	 *
	 * @param record The source jOOQ record
	 * @param aggregate The target aggregate
	 */
	@Suppress("UNCHECKED_CAST")
	protected open fun toAggregate(
		record: UpdatableRecord<*>,
		aggregate: A,
	) {
		aggregate.setValueByPath("id", record.getField("id"))
		aggregate.setValueByPath("version", record.getField("version"))
		aggregate.setValueByPath("tenantId", record.getField("tenantId"))
		aggregate.setValueByPath("ownerId", record.getField("ownerId"))
		aggregate.setValueByPath("caption", record.getField("caption"))
		aggregate.setValueByPath("createdByUserId", record.getField("createdByUserId"))
		aggregate.setValueByPath("createdAt", record.getField("createdAt"))
		aggregate.setValueByPath("modifiedByUserId", record.getField("modifiedByUserId"))
		aggregate.setValueByPath("modifiedAt", record.getField("modifiedAt"))
	}

	/**
	 * Stores an aggregate to the database.
	 * All operations are wrapped in a transaction to ensure atomicity.
	 *
	 * @param aggregate The aggregate to store
	 */
	override fun doStore(aggregate: A) {
		dslContext().transaction { _ ->
			val record = fromAggregate(aggregate)
			try {
				record.store()
				storeExtension(aggregate)
				System.err.println("stored:\n$record")
			} catch (e: RuntimeException) {
				System.err.println("${e.message}:\n$record")
				throw e // Re-throw to trigger rollback
			}
		}
	}

	/**
	 * Creates a jOOQ record from the aggregate for storage.
	 *
	 * @param aggregate The aggregate to convert
	 * @return The jOOQ record
	 */
	protected abstract fun fromAggregate(aggregate: A): UpdatableRecord<*>

	/**
	 * Stores any extension data for the aggregate (e.g., extension table records).
	 * Default implementation does nothing - override in subclasses if needed.
	 *
	 * @param aggregate The aggregate whose extension data should be stored
	 */
	protected open fun storeExtension(aggregate: A) {
		// Default: no extension data to store
	}

	/**
	 * Helper to get a field value from a jOOQ record by getter name.
	 */
	protected fun UpdatableRecord<*>.getField(
		fieldName: String,
	): Any? {
		val getterName = "get${fieldName.replaceFirstChar { it.uppercase() }}"
		return try {
			val method = this.javaClass.getMethod(getterName)
			method.invoke(this)
		} catch (e: NoSuchMethodException) {
			null
		} catch (e: IllegalAccessException) {
			throw RuntimeException(e)
		} catch (e: InvocationTargetException) {
			throw RuntimeException(e)
		}
	}

	/**
	 * Helper to set a field value on a jOOQ record by setter name.
	 */
	protected fun setRecordField(
		record: UpdatableRecord<*>,
		fieldName: String,
		value: Any?,
	) {
		val setterName = "set${fieldName.replaceFirstChar { it.uppercase() }}"
		try {
			// Find the setter method (may need to handle multiple overloads)
			val methods = record.javaClass.methods.filter { it.name == setterName && it.parameterCount == 1 }
			if (methods.isNotEmpty()) {
				methods.first().invoke(record, value)
			}
		} catch (e: IllegalAccessException) {
			throw RuntimeException(e)
		} catch (e: InvocationTargetException) {
			throw RuntimeException(e)
		}
	}

}
