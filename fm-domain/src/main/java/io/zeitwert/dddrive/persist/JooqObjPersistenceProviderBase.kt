package io.zeitwert.dddrive.persist

import io.dddrive.core.obj.model.Obj
import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath
import io.zeitwert.fm.obj.model.db.Tables
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import org.jooq.UpdatableRecord

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
		record.id = aggregate.getValueByPath("id")
		record.objTypeId = getAggregateTypeId()
		record.tenantId = aggregate.getValueByPath("tenantId")
		record.version = aggregate.getValueByPath("version") ?: 0
		record.ownerId = aggregate.getValueByPath("owner.id")
		record.caption = aggregate.getValueByPath("caption")
		record.createdByUserId = aggregate.getValueByPath("createdByUser.id")
		record.createdAt = aggregate.getValueByPath("createdAt")
		record.modifiedByUserId = aggregate.getValueByPath("modifiedByUser.id")
		record.modifiedAt = aggregate.getValueByPath("modifiedAt")

		// Obj-specific fields
		record.closedByUserId = aggregate.getValueByPath("closedByUserId")
		record.closedAt = aggregate.getValueByPath("closedAt")

		// Account ID (FM-specific, may be null for some obj types)
		if (aggregate.hasProperty("accountId")) {
			record.accountId = aggregate.getValueByPath("accountId")
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

		aggregate.setValueByPath("objTypeId", objRecord.objTypeId)
		aggregate.setValueByPath("closedByUserId", objRecord.closedByUserId)
		aggregate.setValueByPath("closedAt", objRecord.closedAt)

		if (aggregate.hasProperty("accountId")) {
			aggregate.setValueByPath("accountId", objRecord.accountId)
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
