package fm.comunas.fm.obj.model;

import org.jooq.Record;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.property.model.enums.CodePartListType;

public interface FMObjRepository<O extends Obj, V extends Record> extends ObjRepository<O, V> {

	ObjPartNoteRepository getNoteRepository();

	CodePartListType getNoteListType();

}
