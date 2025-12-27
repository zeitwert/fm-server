package io.zeitwert.fm.obj.persist

import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.obj.model.db.Tables
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import org.jooq.DSLContext

class ObjRecordMapperImpl(
	val dslContext: DSLContext,
) : SqlRecordMapper<Aggregate>,
	SqlIdProvider {

	override fun nextAggregateId(): Any = dslContext.nextval(Sequences.OBJ_ID_SEQ).toInt()

	override fun <P : Part<*>> nextPartId(
		aggregate: Aggregate,
		partClass: Class<P>,
	): Int = dslContext.nextval(Sequences.OBJ_PART_ID_SEQ).toInt()
	// (aggregate as AggregateSPI).nextPartId(partClass)

	override fun loadRecord(aggregate: Aggregate) {
		val record = dslContext.fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: Aggregate,
		record: ObjRecord,
	) {
		// check(aggregate.id != null) { "id defined" }
		// check(aggregate.meta.objTypeId != null) { "objTypeId defined" }
		// obj
		aggregate.setValueByPath("id", record.id)
		aggregate.setValueByPath("tenantId", record.tenantId)
		if (aggregate is ItemWithAccount) {
			aggregate.accountId = record.accountId
		}
		aggregate.setValueByPath("ownerId", record.ownerId)
		aggregate.setValueByPath("caption", record.caption)
		// obj_meta
		aggregate.setValueByPath("version", record.version)
		aggregate.setValueByPath("createdAt", record.createdAt)
		aggregate.setValueByPath("createdByUserId", record.createdByUserId)
		aggregate.setValueByPath("modifiedAt", record.modifiedAt)
		aggregate.setValueByPath("modifiedByUserId", record.modifiedByUserId)
		aggregate.setValueByPath("closedAt", record.closedAt)
		aggregate.setValueByPath("closedByUserId", record.closedByUserId)
	}

	override fun storeRecord(aggregate: Aggregate) {
		val record = mapToRecord(aggregate)
		if (aggregate.meta.isNew) {
			record.insert()
		} else {
			record.changed(true)
			record.update()
		}
		// After store(), JOOQ has updated the record with the new version
		// Refresh the version in the aggregate so it can be used for parts
		(aggregate as Obj).setValueByPath("version", record.version)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: Aggregate): ObjRecord {
		val record = dslContext.newRecord(Tables.OBJ)
		aggregate as Obj

		record.id = aggregate.id as Int
		record.objTypeId = aggregate.meta.objTypeId
		record.tenantId = aggregate.tenantId as Int
		if (aggregate is ItemWithAccount) {
			record.accountId = aggregate.accountId as Int?
		}
		record.ownerId = aggregate.owner?.id as Int?
		record.caption = aggregate.caption

		record.version = aggregate.meta.version
		record.createdAt = aggregate.meta.createdAt
		record.createdByUserId = aggregate.meta.createdByUser?.id as Int?
		record.modifiedAt = aggregate.meta.modifiedAt
		record.modifiedByUserId = aggregate.meta.modifiedByUser?.id as? Int
		record.closedAt = aggregate.meta.closedAt
		record.closedByUserId = aggregate.meta.closedByUser?.id as? Int
		return record
	}

	override fun getAll(tenantId: Any): List<Any> = throw UnsupportedOperationException()

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ.TENANT_ID
			"accountId" -> Tables.OBJ.ACCOUNT_ID
			"ownerId" -> Tables.OBJ.OWNER_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ.ID)
			.from(Tables.OBJ)
			.where(field.eq(targetId as Int))
			.and(Tables.OBJ.OBJ_TYPE_ID.eq(aggregateTypeId))
			.and(Tables.OBJ.CLOSED_AT.isNull)
			.fetch(Tables.OBJ.ID)
	}

}
