package io.zeitwert.fm.collaboration.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.util.List;

import io.dddrive.ddd.model.Aggregate;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

public interface AggregateWithNotesMixin extends ItemWithNotes {

	Aggregate aggregate();

	default ObjNoteRepository noteRepository() {
		return this.aggregate().getMeta().getAppContext().getBean(ObjNoteRepository.class);
	}

	@Override
	default List<ObjNoteVRecord> getNotes() {
		return this.noteRepository().getByForeignKey("related_to_id", this.aggregate().getId());
	}

	@Override
	default ObjNote addNote(CodeNoteType noteType) {
		ObjNote note = this.noteRepository().create(this.aggregate().getTenantId());
		note.setNoteType(noteType);
		note.setRelatedToId(this.aggregate().getId());
		return note;
	}

	@Override
	default void removeNote(Integer noteId) {
		ObjNote note = this.noteRepository().get(noteId);
		requireThis(this.aggregate().getId().equals(note.getRelatedToId()), "Note is related to this item.");
		this.noteRepository().delete(note);
	}

}
