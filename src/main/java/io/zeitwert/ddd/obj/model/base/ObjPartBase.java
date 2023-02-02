
package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.ddd.persistence.jooq.PartState;

public abstract class ObjPartBase<O extends Obj> extends PartBase<O> implements ObjPart<O> {

	protected ObjPartBase(PartRepository<O, ?> repository, O obj, PartState state) {
		super(repository, obj, state);
	}

}
