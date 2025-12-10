package io.zeitwert.fm.collaboration.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.core.ddd.model.Aggregate;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

public interface AggregateWithNotesMixin extends ItemWithNotes {

	Aggregate aggregate();

	ObjNoteRepository noteRepository();

	@Override
	default List<ObjNote> getNotes() {
		return this.noteRepository().getByForeignKey("relatedToId", this.aggregate().getId());
	}

	@Override
	default ObjNote addNote(CodeNoteType noteType) {
		ObjNote note = this.noteRepository().create((Integer) this.aggregate().getTenantId(), null, OffsetDateTime.now());
		note.setNoteType(noteType);
		note.setRelatedToId(this.aggregate().getId());
		return note;
	}

	@Override
	default void removeNote(Object noteId) {
		ObjNote note = this.noteRepository().load(noteId);
		requireThis(this.aggregate().getId().equals(note.getRelatedToId()), "Note is related to this item.");
		this.noteRepository().delete(note, null, OffsetDateTime.now());
	}

}
