package io.zeitwert.ddd.collaboration.model;

import io.zeitwert.ddd.collaboration.model.enums.CodeNoteType;

import java.util.List;

public interface ItemWithNotes {

	List<ObjNote> getNoteList();

	ObjNote addNote(CodeNoteType noteType);

	void removeNote(Integer noteId);

}
