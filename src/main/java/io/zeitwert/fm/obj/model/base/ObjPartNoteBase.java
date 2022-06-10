package io.zeitwert.fm.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.fm.item.model.base.ItemPartNoteBase;
import io.zeitwert.fm.obj.model.ObjPartNote;

import org.jooq.UpdatableRecord;

public abstract class ObjPartNoteBase extends ItemPartNoteBase<Obj> implements ObjPartNote {

	public ObjPartNoteBase(PartRepository<Obj, ?> repository, Obj obj, UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);
	}

}
