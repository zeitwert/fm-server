package io.zeitwert.fm.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

public abstract class FMObjBase extends ObjBase implements FMObj {

	protected FMObjBase(SessionInfo sessionInfo, ObjRepository<? extends Obj, ? extends Record> repository,
			UpdatableRecord<?> objRecord) {
		super(sessionInfo, repository, objRecord);
	}

	@Override
	@SuppressWarnings("unchecked")
	public FMObjRepository<? extends FMObj, ? extends Record> getRepository() {
		return (FMObjRepository<? extends FMObj, ? extends Record>) super.getRepository();
	}

}
