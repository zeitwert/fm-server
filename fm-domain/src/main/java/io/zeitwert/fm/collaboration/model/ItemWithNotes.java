package io.zeitwert.fm.collaboration.model;

import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

import java.util.List;

public interface ItemWithNotes {

	List<ObjNoteVRecord> getNotes();

	ObjNote addNote(CodeNoteType noteType);

	void removeNote(Integer noteId);

}
