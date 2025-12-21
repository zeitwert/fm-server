package io.zeitwert.fm.collaboration.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.obj.model.base.FMObjBase

abstract class ObjNoteBase(
	override val repository: ObjNoteRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjNote {

	override fun doInit() {
		super.doInit()
		addBaseProperty("relatedToId", Any::class.java)
		addEnumProperty("noteType", CodeNoteType::class.java)
		addBaseProperty("subject", String::class.java)
		addBaseProperty("content", String::class.java)
		addBaseProperty("isPrivate", Boolean::class.java)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption("Notiz: " + (subject ?: "Ohne Betreff"))
	}

	// ObjNote interface implementation

	override val relatedTo: Aggregate?
		get() {
			// TODO: Implement via repository directory lookup
			return null
		}

}
