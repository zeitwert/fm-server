package io.zeitwert.fm.collaboration.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.obj.model.base.FMObjBase

abstract class ObjNoteBase(
	repository: ObjNoteRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjNote {

	private val _relatedToId = addBaseProperty("relatedToId", Any::class.java)
	private val _noteType = addEnumProperty("noteType", CodeNoteType::class.java)
	private val _subject = addBaseProperty("subject", String::class.java)
	private val _content = addBaseProperty("content", String::class.java)
	private val _isPrivate = addBaseProperty("isPrivate", Boolean::class.java)

	override val repository get() = super.repository as ObjNoteRepository

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		this._caption.value = "Notiz"
	}

	// ObjNote interface implementation

	override val relatedTo: Aggregate?
		get() {
			// TODO: Implement via repository directory lookup
			return null
		}

}
