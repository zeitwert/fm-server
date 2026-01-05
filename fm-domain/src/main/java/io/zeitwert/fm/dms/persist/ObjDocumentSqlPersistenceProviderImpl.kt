package io.zeitwert.fm.dms.persist

import dddrive.ddd.query.QuerySpec
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.db.Tables
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentRecord
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component

@Component("objDocumentPersistenceProvider")
open class ObjDocumentSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : FMObjSqlPersistenceProviderBase<ObjDocument>(ObjDocument::class.java),
	SqlRecordMapper<ObjDocument> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjDocument) {
		val record = dslContext.fetchOne(Tables.OBJ_DOCUMENT, Tables.OBJ_DOCUMENT.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_DOCUMENT record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjDocument,
		record: ObjDocumentRecord,
	) {
		aggregate.accountId = record.accountId
		aggregate.name = record.name
		aggregate.documentKind = CodeDocumentKind.getDocumentKind(record.documentKindId)
		aggregate.documentCategory = CodeDocumentCategory.getDocumentCategory(record.documentCategoryId)
		aggregate.templateDocumentId = record.templateDocumentId
		aggregate.contentKind = CodeContentKind.getContentKind(record.contentKindId)
	}

	override fun storeRecord(aggregate: ObjDocument) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjDocument): ObjDocumentRecord {
		val record = dslContext.newRecord(Tables.OBJ_DOCUMENT)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as? Int
		record.name = aggregate.name
		record.documentKindId = aggregate.documentKind?.id
		record.documentCategoryId = aggregate.documentCategory?.id
		record.templateDocumentId = aggregate.templateDocumentId as? Int
		record.contentKindId = aggregate.contentKind?.id

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_DOCUMENT_V, Tables.OBJ_DOCUMENT_V.ID, query)

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_DOCUMENT.OBJ_ID)
			.from(Tables.OBJ_DOCUMENT)
			.where(Tables.OBJ_DOCUMENT.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_DOCUMENT.OBJ_ID)

	// Content-related helper methods (used by repository)

	fun getContentType(document: ObjDocument): CodeContentType? {
		val maxVersionNr = dslContext.fetchValue(getContentMaxVersionQuery(document))
		if (maxVersionNr == null) return null

		val query = getContentWithMaxVersionQuery(document)
		val contentTypeId = dslContext.fetchOne(query)?.contentTypeId ?: return null
		return CodeContentType.getContentType(contentTypeId)
	}

	fun getContent(document: ObjDocument): ByteArray? {
		val query = getContentWithMaxVersionQuery(document)
		return dslContext.fetchOne(query)?.content
	}

	fun storeContent(
		document: ObjDocument,
		contentType: CodeContentType?,
		content: ByteArray?,
	) {
		if (contentType == null || content == null) return

		var versionNr = dslContext.fetchValue(getContentMaxVersionQuery(document))
		versionNr = if (versionNr == null) 1 else versionNr + 1

		dslContext
			.insertInto(Tables.OBJ_DOCUMENT_PART_CONTENT)
			.columns(
				Tables.OBJ_DOCUMENT_PART_CONTENT.OBJ_ID,
				Tables.OBJ_DOCUMENT_PART_CONTENT.VERSION_NR,
				Tables.OBJ_DOCUMENT_PART_CONTENT.CONTENT_TYPE_ID,
				Tables.OBJ_DOCUMENT_PART_CONTENT.CONTENT,
				Tables.OBJ_DOCUMENT_PART_CONTENT.CREATED_BY_USER_ID,
			).values(
				document.id as Int,
				versionNr,
				contentType.id,
				content,
				document.meta.createdByUserId as Int,
			).execute()
	}

	private fun getContentWithMaxVersionQuery(document: ObjDocument) =
		Tables.OBJ_DOCUMENT_PART_CONTENT.where(
			Tables.OBJ_DOCUMENT_PART_CONTENT.OBJ_ID
				.eq(document.id as Int)
				.and(Tables.OBJ_DOCUMENT_PART_CONTENT.VERSION_NR.eq(getContentMaxVersionQuery(document))),
		)

	private fun getContentMaxVersionQuery(document: ObjDocument) =
		dslContext
			.select(DSL.max(Tables.OBJ_DOCUMENT_PART_CONTENT.VERSION_NR))
			.from(Tables.OBJ_DOCUMENT_PART_CONTENT)
			.where(Tables.OBJ_DOCUMENT_PART_CONTENT.OBJ_ID.eq(document.id as Int))

}
