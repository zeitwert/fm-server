package fm.comunas.fm.obj.model.base;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.fm.item.model.base.ItemPartNoteBase;
import fm.comunas.fm.obj.model.ObjPartNote;

import org.jooq.UpdatableRecord;

public abstract class ObjPartNoteBase extends ItemPartNoteBase<Obj> implements ObjPartNote {

	public ObjPartNoteBase(Obj obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
	}

}
