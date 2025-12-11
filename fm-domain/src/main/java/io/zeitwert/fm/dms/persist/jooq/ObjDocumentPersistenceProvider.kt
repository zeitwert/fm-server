package io.zeitwert.fm.dms.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.db.Tables
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.db.Sequences
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objDocumentPersistenceProvider")
open class ObjDocumentPersistenceProvider : JooqObjPersistenceProviderBase<ObjDocument>() {

    private lateinit var _dslContext: DSLContext
    private lateinit var _repository: ObjDocumentRepository

    @Autowired
    fun setDslContext(dslContext: DSLContext) {
        this._dslContext = dslContext
    }

    @Autowired
    @Lazy
    fun setRepository(repository: ObjDocumentRepository) {
        this._repository = repository
    }

    override fun dslContext(): DSLContext = _dslContext

    override fun getRepository(): ObjDocumentRepository = _repository

    override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

    override fun nextAggregateId(): Any {
        return dslContext()
            .nextval(Sequences.OBJ_ID_SEQ)
            .toInt()
    }

    override fun fromAggregate(aggregate: ObjDocument): UpdatableRecord<*> {
        return createObjRecord(aggregate)
    }

    @Suppress("UNCHECKED_CAST")
    override fun storeExtension(aggregate: ObjDocument) {
        val objId = aggregate.id as Int

        val existingRecord = dslContext().fetchOne(
            Tables.OBJ_DOCUMENT,
            Tables.OBJ_DOCUMENT.OBJ_ID.eq(objId)
        )

        val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_DOCUMENT)

        record.objId = objId
        record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
        record.name = (aggregate.getProperty("name") as? BaseProperty<String?>)?.value
        record.documentKindId = (aggregate.getProperty("documentKind") as? EnumProperty<CodeDocumentKind>)?.value?.id
        record.documentCategoryId = (aggregate.getProperty("documentCategory") as? EnumProperty<CodeDocumentCategory>)?.value?.id
        record.templateDocumentId = (aggregate.getProperty("templateDocument") as? ReferenceProperty<*>)?.id as? Int
        record.contentKindId = (aggregate.getProperty("contentKind") as? EnumProperty<CodeContentKind>)?.value?.id

        if (existingRecord != null) {
            record.update()
        } else {
            record.insert()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadExtension(aggregate: ObjDocument, objId: Int?) {
        if (objId == null) return

        val record = dslContext().fetchOne(
            Tables.OBJ_DOCUMENT,
            Tables.OBJ_DOCUMENT.OBJ_ID.eq(objId)
        ) ?: return

        (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = record.accountId
        (aggregate.getProperty("name") as? BaseProperty<String?>)?.value = record.name

        record.documentKindId?.let { id ->
            (aggregate.getProperty("documentKind") as? EnumProperty<CodeDocumentKind>)?.value =
                CodeDocumentKind.getDocumentKind(id)
        }

        record.documentCategoryId?.let { id ->
            (aggregate.getProperty("documentCategory") as? EnumProperty<CodeDocumentCategory>)?.value =
                CodeDocumentCategory.getDocumentCategory(id)
        }

        (aggregate.getProperty("templateDocument") as? ReferenceProperty<*>)?.let { prop ->
            @Suppress("UNCHECKED_CAST")
            (prop as ReferenceProperty<Any>).id = record.templateDocumentId
        }

        record.contentKindId?.let { id ->
            (aggregate.getProperty("contentKind") as? EnumProperty<CodeContentKind>)?.value =
                CodeContentKind.getContentKind(id)
        }
    }

    fun getContentType(document: ObjDocument): CodeContentType? {
        val maxVersionNr = dslContext().fetchValue(getContentMaxVersionQuery(document))
        if (maxVersionNr == null) return null

        val query = getContentWithMaxVersionQuery(document)
        val contentTypeId = dslContext().fetchOne(query)?.getContentTypeId() ?: return null
        return CodeContentType.getContentType(contentTypeId)
    }

    fun getContent(document: ObjDocument): ByteArray? {
        val query = getContentWithMaxVersionQuery(document)
        return dslContext().fetchOne(query)?.content
    }

    fun storeContent(document: ObjDocument, contentType: CodeContentType?, content: ByteArray?) {
        if (contentType == null || content == null) return

        var versionNr = dslContext().fetchValue(getContentMaxVersionQuery(document))
        versionNr = if (versionNr == null) 1 else versionNr + 1

        dslContext()
            .insertInto(Tables.OBJ_DOCUMENT_PART_CONTENT)
            .columns(
                Tables.OBJ_DOCUMENT_PART_CONTENT.OBJ_ID,
                Tables.OBJ_DOCUMENT_PART_CONTENT.VERSION_NR,
                Tables.OBJ_DOCUMENT_PART_CONTENT.CONTENT_TYPE_ID,
                Tables.OBJ_DOCUMENT_PART_CONTENT.CONTENT,
                Tables.OBJ_DOCUMENT_PART_CONTENT.CREATED_BY_USER_ID
            )
            .values(
                document.id as Int,
                versionNr,
                contentType.id,
                content,
                document.meta.createdByUser?.id as Int
            )
            .execute()
    }

    private fun getContentWithMaxVersionQuery(document: ObjDocument) =
        Tables.OBJ_DOCUMENT_PART_CONTENT.where(
            Tables.OBJ_DOCUMENT_PART_CONTENT.OBJ_ID.eq(document.id as Int)
                .and(Tables.OBJ_DOCUMENT_PART_CONTENT.VERSION_NR.eq(getContentMaxVersionQuery(document)))
        )

    private fun getContentMaxVersionQuery(document: ObjDocument) =
        dslContext()
            .select(DSL.max(Tables.OBJ_DOCUMENT_PART_CONTENT.VERSION_NR))
            .from(Tables.OBJ_DOCUMENT_PART_CONTENT)
            .where(Tables.OBJ_DOCUMENT_PART_CONTENT.OBJ_ID.eq(document.id as Int))

    override fun getRecordIdsByForeignKey(fkName: String, targetId: Int): List<Int> {
        if (fkName == "accountId") {
            return dslContext()
                .select(Tables.OBJ_DOCUMENT.OBJ_ID)
                .from(Tables.OBJ_DOCUMENT)
                .where(Tables.OBJ_DOCUMENT.ACCOUNT_ID.eq(targetId))
                .fetch(Tables.OBJ_DOCUMENT.OBJ_ID)
        }
        if (fkName == "templateDocumentId") {
            return dslContext()
                .select(Tables.OBJ_DOCUMENT.OBJ_ID)
                .from(Tables.OBJ_DOCUMENT)
                .where(Tables.OBJ_DOCUMENT.TEMPLATE_DOCUMENT_ID.eq(targetId))
                .fetch(Tables.OBJ_DOCUMENT.OBJ_ID)
        }
        return super.getRecordIdsByForeignKey(fkName, targetId)
    }

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_document"
    }
}

