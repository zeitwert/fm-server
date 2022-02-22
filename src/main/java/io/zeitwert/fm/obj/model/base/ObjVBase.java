package io.zeitwert.fm.obj.model.base;

import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.session.model.SessionInfo;

import org.jooq.UpdatableRecord;

public abstract class ObjVBase extends ObjBase {

	public ObjVBase(SessionInfo sessionInfo, ObjVRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> extnRecord) {
		super(sessionInfo, repository, objRecord);
	}

}
