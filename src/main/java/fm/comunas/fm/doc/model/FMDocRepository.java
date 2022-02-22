package fm.comunas.fm.doc.model;

import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.property.model.enums.CodePartListType;

import org.jooq.Record;

public interface FMDocRepository<D extends Doc, V extends Record> extends fm.comunas.ddd.doc.model.DocRepository<D, V> {

	DocPartNoteRepository getNoteRepository();

	CodePartListType getNoteListType();

}
