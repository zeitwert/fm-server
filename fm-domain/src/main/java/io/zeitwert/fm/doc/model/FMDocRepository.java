package io.zeitwert.fm.doc.model;

import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocRepository;
// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
// import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public interface FMDocRepository<D extends Doc, V extends Object> extends DocRepository<D, V> {

	DocTaskRepository getTaskRepository();

	// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
	// ObjNoteRepository getNoteRepository();

}
