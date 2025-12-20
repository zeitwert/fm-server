package io.zeitwert.fm.collaboration.persist.jooq

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.db.Tables
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.springframework.stereotype.Component

/** Persistence provider for ObjNote aggregates. */
@Component("objNotePersistenceProvider")
open class ObjNotePersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjNote, ObjRecord, ObjNoteRecord>(ObjNote::class.java),
	SqlAggregateRecordMapper<ObjNote, ObjNoteRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjNoteRecord {
		val record = dslContext.fetchOne(Tables.OBJ_NOTE, Tables.OBJ_NOTE.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_NOTE record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjNote,
		record: ObjNoteRecord,
	) {
		aggregate.setValueByPath("relatedToId", record.relatedToId)
		aggregate.setValueByPath("noteType", CodeNoteType.getNoteType(record.noteTypeId))
		aggregate.setValueByPath("subject", record.subject)
		aggregate.setValueByPath("content", record.content)
		aggregate.setValueByPath("isPrivate", record.isPrivate)
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjNote): ObjNoteRecord {
		val record = dslContext.newRecord(Tables.OBJ_NOTE)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.relatedToId = aggregate.relatedToId as? Int
		record.noteTypeId = aggregate.noteType?.id
		record.subject = aggregate.subject
		record.content = aggregate.content
		record.isPrivate = aggregate.isPrivate

		return record
	}

	override fun storeRecord(
		record: ObjNoteRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_NOTE.OBJ_ID)
			.from(Tables.OBJ_NOTE)
			.where(Tables.OBJ_NOTE.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_NOTE.OBJ_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ_NOTE.TENANT_ID
			"relatedToId" -> Tables.OBJ_NOTE.RELATED_TO_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ_NOTE.OBJ_ID)
			.from(Tables.OBJ_NOTE)
			.where(field.eq(targetId as Int))
			.fetch(Tables.OBJ_NOTE.OBJ_ID)
	}

}

