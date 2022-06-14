package io.zeitwert.fm.obj.model;

import org.jooq.Record;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;

public interface FMObjRepository<O extends Obj, V extends Record> extends ObjRepository<O, V> {
}
