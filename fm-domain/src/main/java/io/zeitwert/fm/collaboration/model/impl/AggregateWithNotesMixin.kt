package io.zeitwert.fm.collaboration.model.impl

import io.dddrive.core.ddd.model.Aggregate
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType
import java.time.OffsetDateTime

interface AggregateWithNotesMixin : ItemWithNotes {

	fun aggregate(): Aggregate

	fun noteRepository(): ObjNoteRepository

	override val notes: List<ObjNote>
		get() =
			noteRepository()
				.getByForeignKey("relatedToId", aggregate().id)
				.stream()
				.map { it: Any -> noteRepository().get(it) }
				.toList()

	override fun addNote(
		noteType: CodeNoteType,
		userId: Any,
	): ObjNote {
		val note = noteRepository().create(aggregate().tenantId, userId, OffsetDateTime.now())
		note.noteType = noteType
		note.relatedToId = aggregate().id
		return note
	}

	override fun removeNote(
		noteId: Any,
		userId: Any,
	) {
		val note = noteRepository().load(noteId)
		require(aggregate().id.equals(note.relatedToId)) { "note is related to this item." }
		noteRepository().delete(note, userId, OffsetDateTime.now())
	}

}
