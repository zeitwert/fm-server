package io.zeitwert.fm.obj.model;

import org.jooq.Record;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;

public interface FMObjRepository<O extends Obj, V extends Record> extends ObjRepository<O, V> {

	CodePartListType getAreaSetType();

	ObjNoteRepository getNoteRepository();

}
