package io.zeitwert.fm.obj.model.base;

import org.jooq.DSLContext;

import io.dddrive.app.service.api.AppContext;
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
			String partTypeId,
			AppContext appContext,
			DSLContext dslContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
	}

	@Override
	public Integer nextPartId() {
		return this.dslContext().nextval(OBJ_PART_ID_SEQ).intValue();
	}

}
