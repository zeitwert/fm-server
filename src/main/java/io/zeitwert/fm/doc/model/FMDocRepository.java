package io.zeitwert.fm.doc.model;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;

import org.jooq.Record;

public interface FMDocRepository<D extends Doc, V extends Record> extends DocRepository<D, V> {

	ObjNoteRepository getNoteRepository();

}
