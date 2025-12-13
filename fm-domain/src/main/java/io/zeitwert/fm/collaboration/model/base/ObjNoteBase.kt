package io.zeitwert.fm.collaboration.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.obj.model.base.FMObjBase

/**
 * Base class for ObjNote using the NEW dddrive framework.
 */
abstract class ObjNoteBase(
    repository: ObjNoteRepository
) : FMObjBase(repository), ObjNote {

    //@formatter:off
    private val _relatedToId: BaseProperty<Any> = this.addBaseProperty("relatedToId", Any::class.java)
    private val _noteType: EnumProperty<CodeNoteType> = this.addEnumProperty("noteType", CodeNoteType::class.java)
    private val _subject: BaseProperty<String> = this.addBaseProperty("subject", String::class.java)
    private val _content: BaseProperty<String> = this.addBaseProperty("content", String::class.java)
    private val _isPrivate: BaseProperty<Boolean> = this.addBaseProperty("isPrivate", Boolean::class.java)
    //@formatter:on

    override fun getRepository(): ObjNoteRepository {
        return super.getRepository() as ObjNoteRepository
    }

    override fun doCalcAll() {
        super.doCalcAll()
        this.calcCaption()
    }

    private fun calcCaption() {
        this.caption.value = "Notiz"
    }

    // ObjNote interface implementation

    override fun getRelatedToId(): Any? = _relatedToId.value

    override fun setRelatedToId(id: Any?) {
        _relatedToId.value = id
    }

    override fun getRelated(): Aggregate? {
        // TODO: Implement via repository directory lookup
        return null
    }

    override fun getNoteType(): CodeNoteType? = _noteType.value

    override fun setNoteType(noteType: CodeNoteType?) {
        _noteType.value = noteType
    }

    fun getNoteTypeId(): String? = _noteType.value?.id

    override fun getSubject(): String? = _subject.value

    override fun setSubject(subject: String?) {
        _subject.value = subject
    }

    override fun getContent(): String? = _content.value

    override fun setContent(content: String?) {
        _content.value = content
    }

    override fun getIsPrivate(): Boolean? = _isPrivate.value

    override fun setIsPrivate(isPrivate: Boolean?) {
        _isPrivate.value = isPrivate
    }
}

