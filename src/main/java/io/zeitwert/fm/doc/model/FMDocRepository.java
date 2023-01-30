package io.zeitwert.fm.doc.model;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

import org.jooq.TableRecord;

public interface FMDocRepository<D extends Doc, V extends TableRecord<?>> extends DocRepository<D, V> {

	ObjNoteRepository getNoteRepository();

	DocTaskRepository getTaskRepository();

}
