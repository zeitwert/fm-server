package io.dddrive.jooq.obj;

import io.dddrive.jooq.ddd.JooqPartRepositoryBase;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPart;

public abstract class JooqObjPartRepositoryBase<O extends Obj, P extends ObjPart<O>>
		extends JooqPartRepositoryBase<O, P>
		implements ObjPartPropertyProviderMixin {

	protected JooqObjPartRepositoryBase(
			Class<? extends O> aggregateIntfClass,
			Class<? extends ObjPart<O>> intfClass,
			Class<? extends ObjPart<O>> baseClass,
			String partTypeId) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId);
		this.mapProperties();
	}

}
