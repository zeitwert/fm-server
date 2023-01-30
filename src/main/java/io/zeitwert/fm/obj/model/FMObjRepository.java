package io.zeitwert.fm.obj.model;

import org.jooq.TableRecord;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public interface FMObjRepository<O extends Obj, V extends TableRecord<?>> extends ObjRepository<O, V> {

	ObjNoteRepository getNoteRepository();

	DocTaskRepository getTaskRepository();

}
