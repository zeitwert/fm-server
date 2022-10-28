package io.zeitwert.fm.obj.model.base;

import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.session.model.RequestContext;

import org.jooq.UpdatableRecord;

public abstract class ObjVBase extends ObjBase {

	public ObjVBase(RequestContext requestCtx, ObjVRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> extnRecord) {
		super(requestCtx, repository, objRecord);
	}

}
