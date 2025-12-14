package io.zeitwert.fm.collaboration.model.impl;

import io.dddrive.core.ddd.model.Aggregate;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

import java.time.OffsetDateTime;
import java.util.List;

import static io.dddrive.util.Invariant.requireThis;

public interface AggregateWithNotesMixin extends ItemWithNotes {

	Aggregate aggregate();

	ObjNoteRepository noteRepository();

	@Override
	default List<ObjNote> getNotes() {
		return noteRepository().getByForeignKey("relatedToId", aggregate().getId()).stream().map(it -> noteRepository().get(it)).toList();
	}

	@Override
	default ObjNote addNote(CodeNoteType noteType, Object userId) {
		ObjNote note = noteRepository().create(aggregate().getTenantId(), userId, OffsetDateTime.now());
		note.setNoteType(noteType);
		note.setRelatedToId(aggregate().getId());
		return note;
	}

	@Override
	default void removeNote(Object noteId, Object userId) {
		ObjNote note = noteRepository().load(noteId);
		requireThis(aggregate().getId().equals(note.getRelatedToId()), "Note is related to this item.");
		noteRepository().delete(note, userId, OffsetDateTime.now());
	}

}
