package io.zeitwert.persist.sql

import dddrive.query.QuerySpec
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.db.Tables
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.persist.ObjNotePersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.obj.ObjRecordMapperImpl
import io.zeitwert.persist.sql.obj.ObjSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/** Persistence provider for ObjNote aggregates. */
@Component("objNotePersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "sql", matchIfMissing = true)
open class ObjNoteSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjSqlPersistenceProviderBase<ObjNote>(ObjNote::class.java),
	SqlRecordMapper<ObjNote>,
	ObjNotePersistenceProvider {

	override val hasAccount = false

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
		record.relatedToId = aggregate.relatedToId as Int
		record.noteTypeId = aggregate.noteType?.id
		record.subject = aggregate.subject
		record.content = aggregate.content
		record.isPrivate = aggregate.isPrivate

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_NOTE_V, Tables.OBJ_NOTE_V.ID, query)

}
