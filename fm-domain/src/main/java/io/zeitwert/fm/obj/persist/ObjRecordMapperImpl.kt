package io.zeitwert.fm.obj.persist

import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.path.setValueByPath
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

	fun isObj(id: Any) = dslContext.fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(id as Int)) != null

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
		// aggregate fields
		aggregate.setValueByPath("id", record.id)
		aggregate.setValueByPath("tenantId", record.tenantId)
		if (aggregate is ItemWithAccount) {
			aggregate.accountId = record.accountId
		}
		aggregate.setValueByPath("ownerId", record.ownerId)
		aggregate.setValueByPath("caption", record.caption)
		aggregate.setValueByPath("version", record.version)
		// obj-specific fields
		aggregate.setValueByPath("objTypeId", record.objTypeId)
		// obj_meta
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
		record.ownerId = aggregate.ownerId as Int?
		record.caption = aggregate.caption

		record.version = aggregate.meta.version
		record.createdAt = aggregate.meta.createdAt
		record.createdByUserId = aggregate.meta.createdByUserId as Int
		record.modifiedAt = aggregate.meta.modifiedAt
		record.modifiedByUserId = aggregate.meta.modifiedByUserId as Int?
		record.closedAt = aggregate.meta.closedAt
		record.closedByUserId = aggregate.meta.closedByUserId as Int?
		return record
	}

}
