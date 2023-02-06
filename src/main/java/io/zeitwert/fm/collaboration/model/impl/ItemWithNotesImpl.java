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

public class ItemWithNotesImpl implements ItemWithNotes {

	private final Aggregate aggregate;
	private final ObjNoteRepository noteRepository;

	public ItemWithNotesImpl(Aggregate aggregate) {
		this.aggregate = aggregate;
		this.noteRepository = AppContext.getInstance().getBean(ObjNoteRepository.class);
	}

	@Override
	public List<ObjNoteVRecord> getNotes() {
		return this.noteRepository.getByForeignKey("related_to_id", this.aggregate.getId());
	}

	@Override
	public ObjNote addNote(CodeNoteType noteType) {
		ObjNote note = this.noteRepository.create(this.aggregate.getTenantId());
		note.setNoteType(noteType);
		note.setRelatedToId(this.aggregate.getId());
		return note;
	}

	@Override
	public void removeNote(Integer noteId) {
		ObjNote note = this.noteRepository.get(noteId);
		requireThis(this.aggregate.getId().equals(note.getRelatedToId()), "Note is related to this item.");
		this.noteRepository.delete(note);
	}

}
