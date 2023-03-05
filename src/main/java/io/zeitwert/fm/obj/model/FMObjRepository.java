package io.zeitwert.fm.obj.model;

import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public interface FMObjRepository<O extends Obj, V extends Object> extends ObjRepository<O, V> {

	DocTaskRepository getTaskRepository();

	ObjNoteRepository getNoteRepository();

}
