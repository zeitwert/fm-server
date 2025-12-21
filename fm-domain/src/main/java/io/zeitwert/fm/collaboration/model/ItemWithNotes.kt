package io.zeitwert.fm.collaboration.model

import io.zeitwert.fm.collaboration.model.enums.CodeNoteType

interface ItemWithNotes {

	val notes: List<Any>

	fun addNote(
		noteType: CodeNoteType,
		userId: Any,
	): ObjNote

	fun removeNote(
		noteId: Any,
		userId: Any,
	)

}
