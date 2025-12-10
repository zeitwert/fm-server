package io.zeitwert.fm.doc.model;

import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public interface FMDocRepository<D extends Doc, V extends Object> extends DocRepository<D, V> {

	DocTaskRepository getTaskRepository();

	ObjNoteRepository getNoteRepository();

}
