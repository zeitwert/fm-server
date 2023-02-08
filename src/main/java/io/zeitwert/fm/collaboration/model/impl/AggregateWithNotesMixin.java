package io.zeitwert.fm.collaboration.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

public interface AggregateWithNotesMixin extends ItemWithNotes {

	Aggregate aggregate();

	static ObjNoteRepository noteRepository() {
		return AppContext.getInstance().getBean(ObjNoteRepository.class);
	}

	@Override
	default List<ObjNoteVRecord> getNotes() {
		return noteRepository().getByForeignKey("related_to_id", this.aggregate().getId());
	}

	@Override
	default ObjNote addNote(CodeNoteType noteType) {
		ObjNote note = noteRepository().create(this.aggregate().getTenantId());
		note.setNoteType(noteType);
		note.setRelatedToId(this.aggregate().getId());
		return note;
	}

	@Override
	default void removeNote(Integer noteId) {
		ObjNote note = noteRepository().get(noteId);
		requireThis(this.aggregate().getId().equals(note.getRelatedToId()), "Note is related to this item.");
		noteRepository().delete(note);
	}

}
