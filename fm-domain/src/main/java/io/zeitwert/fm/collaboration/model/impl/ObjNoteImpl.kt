package io.zeitwert.fm.collaboration.model.impl

import io.dddrive.ddd.model.Aggregate
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.obj.model.base.FMObjBase

open class ObjNoteImpl(
	override val repository: ObjNoteRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjNote {

	override var relatedToId: Any? by baseProperty()
	override var noteType: CodeNoteType? by enumProperty()
	override var subject: String? by baseProperty()
	override var content: String? by baseProperty()
	override var isPrivate: Boolean? by baseProperty()

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
