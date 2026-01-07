package io.zeitwert.fm.collaboration.model.impl

import dddrive.app.ddd.model.Aggregate
import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import io.zeitwert.fm.doc.model.FMDocVRepository
import io.zeitwert.fm.obj.model.FMObjVRepository
import io.zeitwert.fm.obj.model.base.FMObjBase

class ObjNoteImpl(
	override val repository: ObjNoteRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjNote {

	override var relatedToId by baseProperty<Any>("relatedToId")
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

	override val relatedTo: Aggregate?
		get() {
			if (relatedToId == null) return null
			val objRepository = directory.getRepository(Obj::class.java) as FMObjVRepository
			if (objRepository.isObj(relatedToId!!)) {
				return objRepository.get(relatedToId!!)
			}
			val docRepository = directory.getRepository(Doc::class.java) as FMDocVRepository
			require(docRepository.isDoc(relatedToId!!)) { "relatedToId must refer to Obj or Doc" }
			return docRepository.get(relatedToId!!)
		}

}
