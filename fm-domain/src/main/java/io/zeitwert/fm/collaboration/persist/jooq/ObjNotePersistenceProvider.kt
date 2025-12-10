package io.zeitwert.fm.collaboration.persist.jooq

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.db.Tables
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum
import io.zeitwert.fm.obj.model.db.Sequences
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * jOOQ-based persistence provider for ObjNote aggregates.
 */
@Component("objNotePersistenceProvider")
open class ObjNotePersistenceProvider : JooqObjPersistenceProviderBase<ObjNote>() {

    private lateinit var _dslContext: DSLContext
    private lateinit var _repository: ObjNoteRepository

    @Autowired
    fun setDslContext(dslContext: DSLContext) {
        this._dslContext = dslContext
    }

    @Autowired
    @Lazy
    fun setRepository(repository: ObjNoteRepository) {
        this._repository = repository
    }

    override fun dslContext(): DSLContext = _dslContext

    override fun getRepository(): ObjNoteRepository = _repository

    override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

    override fun nextAggregateId(): Any {
        return dslContext()
            .nextval(Sequences.OBJ_ID_SEQ)
            .toInt()
    }

    override fun fromAggregate(aggregate: ObjNote): UpdatableRecord<*> {
        return createObjRecord(aggregate)
    }

    @Suppress("UNCHECKED_CAST")
    override fun storeExtension(aggregate: ObjNote) {
        val objId = aggregate.id as Int

        val existingRecord = dslContext().fetchOne(
            Tables.OBJ_NOTE,
            Tables.OBJ_NOTE.OBJ_ID.eq(objId)
        )

        val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_NOTE)

        record.objId = objId
        record.tenantId = aggregate.tenantId as? Int
        record.relatedToId = (aggregate.getProperty("relatedToId") as? BaseProperty<Int?>)?.value
        record.noteTypeId = (aggregate.getProperty("noteType") as? EnumProperty<CodeNoteType>)?.value?.id
        record.subject = (aggregate.getProperty("subject") as? BaseProperty<String?>)?.value
        record.content = (aggregate.getProperty("content") as? BaseProperty<String?>)?.value
        record.isPrivate = (aggregate.getProperty("isPrivate") as? BaseProperty<Boolean?>)?.value

        if (existingRecord != null) {
            record.update()
        } else {
            record.insert()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadExtension(aggregate: ObjNote, objId: Int?) {
        if (objId == null) return

        val record = dslContext().fetchOne(
            Tables.OBJ_NOTE,
            Tables.OBJ_NOTE.OBJ_ID.eq(objId)
        ) ?: return

        (aggregate.getProperty("relatedToId") as? BaseProperty<Int?>)?.value = record.relatedToId

        record.noteTypeId?.let { noteTypeId ->
            (aggregate.getProperty("noteType") as? EnumProperty<CodeNoteType>)?.value =
                CodeNoteTypeEnum.getNoteType(noteTypeId)
        }

        (aggregate.getProperty("subject") as? BaseProperty<String?>)?.value = record.subject
        (aggregate.getProperty("content") as? BaseProperty<String?>)?.value = record.content
        (aggregate.getProperty("isPrivate") as? BaseProperty<Boolean?>)?.value = record.isPrivate
    }

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_note"
    }
}

