package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.obj.model.ObjPartRepository;
import io.zeitwert.ddd.part.model.base.PartRepositoryBase;

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
