package io.zeitwert.dddrive.ddd.persist.jooq

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.AggregateMeta
import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.ddd.model.AggregateSPI
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.util.Invariant.assertThis
import io.dddrive.util.Invariant.requireThis
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import java.lang.reflect.InvocationTargetException
import java.time.OffsetDateTime

/**
 * Base class for jOOQ-based persistence providers implementing the new dddrive AggregatePersistenceProvider interface.
 *
 * This class provides the foundation for persisting aggregates to PostgreSQL using jOOQ.
 * Concrete implementations should handle specific aggregate types (Obj, Doc) with their
 * respective table structures.
 *
 * Key differences from MongoDB provider:
 * - Uses Integer IDs (PostgreSQL sequences) instead of ObjectId
 * - Works with jOOQ UpdatableRecord instead of PTOs
 * - Delegates to jOOQ DSLContext for database operations
 *
 * @param A The aggregate type
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
		requireThis(isValidId(id)) { "valid id" }
		val record = loadRecord(id as Int)
		assertThis(record != null) { "aggregate found for id ($id)" }

		(aggregate as AggregateMeta).disableCalc()
		try {
			toAggregate(record!!, aggregate)
		} finally {
			(aggregate as AggregateMeta).enableCalc()
			aggregate.calcVolatile()
		}
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
	 * Loads the base record from the database.
	 *
	 * @param id The aggregate ID
	 * @return The loaded record, or null if not found
	 */
	protected abstract fun loadRecord(id: Int): UpdatableRecord<*>?

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
		// Base aggregate fields - these are common to all aggregates
		(aggregate.getProperty("id") as? BaseProperty<Any?>)?.value = getRecordField(record, "id")
		(aggregate.getProperty("version") as? BaseProperty<Int?>)?.value = getRecordField(record, "version") as? Int
		(aggregate.getProperty("tenant") as? ReferenceProperty<ObjTenant>)?.id = getRecordField(record, "tenantId")
		(aggregate.getProperty("owner") as? ReferenceProperty<ObjUser>)?.id = getRecordField(record, "ownerId")
		(aggregate.getProperty("caption") as? BaseProperty<String?>)?.value = getRecordField(record, "caption") as? String
		(aggregate.getProperty("createdByUser") as? ReferenceProperty<ObjUser>)?.id =
			getRecordField(record, "createdByUserId")
		(aggregate.getProperty("createdAt") as? BaseProperty<OffsetDateTime?>)?.value =
			getRecordField(record, "createdAt") as? OffsetDateTime
		(aggregate.getProperty("modifiedByUser") as? ReferenceProperty<ObjUser>)?.id =
			getRecordField(record, "modifiedByUserId")
		(aggregate.getProperty("modifiedAt") as? BaseProperty<OffsetDateTime?>)?.value =
			getRecordField(record, "modifiedAt") as? OffsetDateTime
	}

	/**
	 * Helper to get a field value from a jOOQ record by getter name.
	 */
	protected fun getRecordField(
		record: UpdatableRecord<*>,
		fieldName: String,
	): Any? {
		val getterName = "get${fieldName.replaceFirstChar { it.uppercase() }}"
		return try {
			val method = record.javaClass.getMethod(getterName)
			method.invoke(record)
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
