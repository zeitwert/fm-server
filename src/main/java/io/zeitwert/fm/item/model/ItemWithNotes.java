package io.zeitwert.fm.item.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;

import java.util.List;

public interface ItemWithNotes<A extends Aggregate, N extends ItemPartNote<A>> {

	Integer getNoteCount();

	N getNote(Integer seqNr);

	List<N> getNoteList();

	N getNoteById(Integer noteId);

	void clearNoteList();

	N addNote();

	void removeNote(Integer noteId);

}
