package io.zeitwert.fm.collaboration.model;

// This interface defines note operations that are implemented by AggregateWithNotesMixin.
import java.util.List;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

public interface ItemWithNotes {

	List<ObjNote> getNotes();

	ObjNote addNote(CodeNoteType noteType);

	void removeNote(Object noteId);

}
