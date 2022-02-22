package fm.comunas.fm.obj.model.base;

import fm.comunas.fm.obj.model.ObjVRepository;
import fm.comunas.ddd.obj.model.base.ObjBase;
import fm.comunas.ddd.session.model.SessionInfo;

import org.jooq.UpdatableRecord;

public abstract class ObjVBase extends ObjBase {

	public ObjVBase(SessionInfo sessionInfo, ObjVRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> extnRecord) {
		super(sessionInfo, repository, objRecord);
	}

}
