package io.zeitwert.fm.collaboration.persist

import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.db.Tables
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.springframework.stereotype.Component

/** Persistence provider for ObjNote aggregates. */
@Component("objNotePersistenceProvider")
open class ObjNoteSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : AggregateSqlPersistenceProviderBase<ObjNote>(ObjNote::class.java),
	SqlRecordMapper<ObjNote> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjNote) {
		val record = dslContext.fetchOne(Tables.OBJ_NOTE, Tables.OBJ_NOTE.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_NOTE record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjNote,
		record: ObjNoteRecord,
	) {
		aggregate.relatedToId = record.relatedToId
		aggregate.noteType = CodeNoteType.Enumeration.getNoteType(record.noteTypeId)
		aggregate.subject = record.subject
		aggregate.content = record.content
		aggregate.isPrivate = record.isPrivate
	}

	override fun storeRecord(aggregate: ObjNote) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjNote): ObjNoteRecord {
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
