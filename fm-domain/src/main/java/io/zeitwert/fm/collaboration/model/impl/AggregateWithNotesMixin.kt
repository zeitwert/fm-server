package io.zeitwert.fm.collaboration.model.impl

import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.query.query
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType

interface AggregateWithNotesMixin : ItemWithNotes {

	fun aggregate(): Aggregate

	fun noteRepository(): ObjNoteRepository

	override val notes: List<Any>
		get() {
			val querySpec = query {
				filter { "relatedToId" eq aggregate().id }
			}
			return noteRepository().find(querySpec)
		}

	override fun addNote(
		noteType: CodeNoteType,
		userId: Any,
	): ObjNote {
		val aggregate = aggregate()
		if (aggregate is Obj) aggregate.tenantId else (aggregate as Doc).tenantId
		val note = noteRepository().create()
		note.noteType = noteType
		note.relatedToId = aggregate.id
		return note
	}

	override fun removeNote(
		noteId: Any,
		userId: Any,
	) {
		val note = noteRepository().load(noteId)
		require(aggregate().id == note.relatedToId) { "note is related to this item." }
		noteRepository().close(note)
	}

}
