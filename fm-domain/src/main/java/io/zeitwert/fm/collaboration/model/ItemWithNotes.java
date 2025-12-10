package io.zeitwert.fm.collaboration.model;

// TODO-MIGRATION: Collaboration - uncomment after all domains are migrated and mixin is restored
// This interface defines note operations that are implemented by AggregateWithNotesMixin.

/*
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

import java.util.List;

public interface ItemWithNotes {

	List<ObjNoteVRecord> getNotes();

	ObjNote addNote(CodeNoteType noteType);

	void removeNote(Integer noteId);

}
*/

// Temporary empty interface to allow compilation
public interface ItemWithNotes {
}
