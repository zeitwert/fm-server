package io.dddrive.obj.model.base;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.base.PartRepositoryBase;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPart;
import io.dddrive.obj.model.ObjPartRepository;

public abstract class ObjPartRepositoryBase<O extends Obj, P extends ObjPart<O>> extends PartRepositoryBase<O, P>
		implements ObjPartRepository<O, P> {

	protected ObjPartRepositoryBase(
			Class<? extends O> aggregateIntfClass,
			Class<? extends ObjPart<O>> intfClass,
			Class<? extends ObjPart<O>> baseClass,
			String partTypeId,
			AppContext appContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext);
	}

}
