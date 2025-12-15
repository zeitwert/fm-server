package io.zeitwert.dddrive.persist

import io.dddrive.core.obj.model.Obj
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.obj.model.db.Tables
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import org.jooq.UpdatableRecord
import java.time.OffsetDateTime

/**
 * Base persistence provider for Obj aggregates using jOOQ.
 *
 * This class extends JooqAggregatePersistenceProviderBase to add Obj-specific
 * field handling (objTypeId, closedAt, closedByUser) and works with the OBJ table
 * plus any extension tables defined in subclasses.
 *
 * @param O The Obj aggregate type
 */
abstract class JooqObjPersistenceProviderBase<O : Obj> : JooqAggregatePersistenceProviderBase<O>() {

	/**
	 * Returns the aggregate type ID for this Obj type (e.g., "obj_contact", "obj_building").
	 */
	protected abstract fun getAggregateTypeId(): String

	/**
	 * Creates a new ObjRecord populated from the aggregate.
	 */
	@Suppress("UNCHECKED_CAST")
	protected fun createObjRecord(aggregate: O): ObjRecord {
		val record = dslContext().newRecord(Tables.OBJ)

		// Set base fields
		record.id = aggregate.getProperty("id")?.let { (it as BaseProperty<Int?>).value }
		record.objTypeId = getAggregateTypeId()
		record.tenantId = (aggregate.getProperty("tenant") as? ReferenceProperty<*>)?.id as? Int
		record.version = (aggregate.getProperty("version") as? BaseProperty<Int?>)?.value ?: 0
		record.ownerId = (aggregate.getProperty("owner") as? ReferenceProperty<*>)?.id as? Int
		record.caption = (aggregate.getProperty("caption") as? BaseProperty<String?>)?.value
		record.createdByUserId = (aggregate.getProperty("createdByUser") as? ReferenceProperty<*>)?.id as? Int
		record.createdAt = (aggregate.getProperty("createdAt") as? BaseProperty<OffsetDateTime?>)?.value
		record.modifiedByUserId = (aggregate.getProperty("modifiedByUser") as? ReferenceProperty<*>)?.id as? Int
		record.modifiedAt = (aggregate.getProperty("modifiedAt") as? BaseProperty<OffsetDateTime?>)?.value

		// Obj-specific fields
		record.closedByUserId = (aggregate.getProperty("closedByUser") as? ReferenceProperty<*>)?.id as? Int
		record.closedAt = (aggregate.getProperty("closedAt") as? BaseProperty<OffsetDateTime?>)?.value

		// Account ID (FM-specific, may be null for some obj types)
		if (aggregate.hasProperty("accountId")) {
			record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
		}

		return record
	}

	/**
	 * Loads the OBJ record from the database.
	 */
	override fun loadRecord(id: Int): ObjRecord? = dslContext().fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(id))

	/**
	 * Maps OBJ record fields to the aggregate, including Obj-specific fields.
	 */
	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		record: UpdatableRecord<*>,
		aggregate: O,
	) {
		super.toAggregate(record, aggregate)

		val objRecord = record as ObjRecord

		// Obj-specific fields
		(aggregate.getProperty("objTypeId") as? BaseProperty<String?>)?.value = objRecord.objTypeId
		(aggregate.getProperty("closedByUser") as? ReferenceProperty<ObjUser>)?.id = objRecord.closedByUserId
		(aggregate.getProperty("closedAt") as? BaseProperty<OffsetDateTime?>)?.value = objRecord.closedAt

		// Account ID (FM-specific)
		if (aggregate.hasProperty("accountId")) {
			(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = objRecord.accountId
		}

		// Load maxPartId from some source - this may need to be tracked differently in jOOQ
		// For now, we'll load it from extension data or calculate it
		loadMaxPartId(aggregate)

		// Load extension data if present
		loadExtension(aggregate, objRecord.id)
	}

	/**
	 * Loads or calculates the maxPartId for the aggregate.
	 * Override in subclasses if needed.
	 */
	protected open fun loadMaxPartId(aggregate: O) {
		// Default implementation - subclasses may need to track this differently
	}

	/**
	 * Loads extension data for the aggregate.
	 * Override in subclasses to load from extension tables.
	 */
	protected open fun loadExtension(
		aggregate: O,
		objId: Int?,
	) {
		// Default: no extension data to load
	}

	override fun getAll(tenantId: Any): List<Any> =
		dslContext()
			.select(Tables.OBJ.ID)
			.from(Tables.OBJ)
			.where(Tables.OBJ.TENANT_ID.eq(tenantId as Int))
			.and(Tables.OBJ.OBJ_TYPE_ID.eq(getAggregateTypeId()))
			.fetch(Tables.OBJ.ID)

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ.TENANT_ID
			"accountId" -> Tables.OBJ.ACCOUNT_ID
			"ownerId" -> Tables.OBJ.OWNER_ID
			else -> throw IllegalArgumentException("unknown fkName: $fkName")
		}
		return dslContext()
			.select(Tables.OBJ.ID)
			.from(Tables.OBJ)
			.where(field.eq(targetId as Int))
			.and(Tables.OBJ.OBJ_TYPE_ID.eq(getAggregateTypeId()))
			.fetch(Tables.OBJ.ID)
	}

}
