package io.zeitwert.fm.obj.model;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.item.model.ItemWithNotes;

public interface FMObj extends Obj, ItemWithNotes<Obj, ObjPartNote> {

}
