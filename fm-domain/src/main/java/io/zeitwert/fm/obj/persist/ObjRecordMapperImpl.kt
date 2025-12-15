package io.zeitwert.fm.obj.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.property.model.BaseProperty
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.obj.model.db.Tables
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("objRecordMapperImpl")
class ObjRecordMapperImpl : SqlAggregateRecordMapper<Obj, ObjRecord> {

	private lateinit var dslContext: DSLContext

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this.dslContext = dslContext
	}

	override fun nextId(): Any = dslContext.nextval(Sequences.OBJ_ID_SEQ).toInt()

	override fun loadRecord(aggregateId: Any): ObjRecord {
		val record = dslContext.fetchOne(Tables.OBJ, Tables.OBJ.ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: Obj,
		record: ObjRecord,
	) {
		// check(aggregate.id != null) { "id defined" }
		// check(aggregate.meta.objTypeId != null) { "objTypeId defined" }
		// obj
		aggregate.setValueByPath("id", record.id)
		aggregate.setValueByPath("objTypeId", record.objTypeId)
		aggregate.setValueByPath("tenant.id", record.tenantId)
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.setValueByPath("owner.id", record.ownerId)
		aggregate.setValueByPath("caption", record.caption)
		// obj_meta
		aggregate.setValueByPath("version", record.version)
		aggregate.setValueByPath("createdAt", record.createdAt)
		aggregate.setValueByPath("createdByUser.id", record.createdByUserId)
		aggregate.setValueByPath("modifiedAt", record.modifiedAt)
		aggregate.setValueByPath("modifiedByUser.id", record.modifiedByUserId)
		aggregate.setValueByPath("closedAt", record.closedAt)
		aggregate.setValueByPath("closedByUser.id", record.closedByUserId)
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: Obj): ObjRecord {
		val record = dslContext.newRecord(Tables.OBJ)

		record.id = aggregate.id as Int
		record.objTypeId = aggregate.meta.objTypeId
		record.tenantId = aggregate.tenantId as Int
		if (aggregate.hasProperty("accountId")) {
			record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
		}
		record.ownerId = aggregate.owner.id as Int
		record.caption = aggregate.caption

		record.version = aggregate.meta.version ?: 0
		record.createdAt = aggregate.meta.createdAt
		record.createdByUserId = aggregate.meta.createdByUser.id as Int
		record.modifiedAt = aggregate.meta.modifiedAt
		record.modifiedByUserId = aggregate.meta.modifiedByUser?.id as? Int
		record.closedAt = aggregate.meta.closedAt
		record.closedByUserId = aggregate.meta.closedByUser?.id as? Int
		return record
	}

	override fun storeRecord(
		record: ObjRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			// Mark the record as changed so JOOQ will perform an update
			// The version field is already set to the old version from aggregate.meta.version
			// JOOQ will automatically:
			// - Use version in WHERE clause for optimistic locking
			// - Increment version in SET clause
			// - Throw DataChangedException if no rows updated
			record.changed(true)
			record.store()
		}
		// After store(), JOOQ has updated the record with the new version
		// Refresh the version in the aggregate so it can be used for parts
		(aggregate as Obj).setValueByPath("version", record.version)
	}

	override fun getAll(tenantId: Any): List<Any> = emptyList()

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> = emptyList()

}
