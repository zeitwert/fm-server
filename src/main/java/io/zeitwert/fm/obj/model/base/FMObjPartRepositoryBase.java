package io.zeitwert.fm.obj.model.base;

import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPart;
import io.dddrive.jooq.obj.JooqObjPartRepositoryBase;

public abstract class FMObjPartRepositoryBase<O extends Obj, P extends ObjPart<O>>
		extends JooqObjPartRepositoryBase<O, P> {

	private static final String OBJ_PART_ID_SEQ = "obj_part_id_seq";

	protected FMObjPartRepositoryBase(
			Class<? extends O> aggregateIntfClass,
			Class<? extends ObjPart<O>> intfClass,
			Class<? extends ObjPart<O>> baseClass,
			String partTypeId) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId);
	}

	@Override
	public Integer nextPartId() {
		return this.dslContext().nextval(OBJ_PART_ID_SEQ).intValue();
	}

}
