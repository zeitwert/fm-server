package io.zeitwert.fm.collaboration.model.impl

import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.obj.model.base.FMObjBase

class ObjNoteImpl(
	override val repository: ObjNoteRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjNote {

	override var relatedToId by referenceIdProperty<Obj>("relatedTo")
	override var noteType by enumProperty<CodeNoteType>("noteType")
	override var subject by baseProperty<String>("subject")
	override var content by baseProperty<String>("content")
	override var isPrivate by baseProperty<Boolean>("isPrivate")

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption("Notiz: " + (subject ?: "Ohne Betreff"))
	}

	// ObjNote interface implementation

	override val relatedTo: Aggregate? = TODO()

}
