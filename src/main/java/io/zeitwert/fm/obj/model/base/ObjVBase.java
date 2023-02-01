package io.zeitwert.fm.obj.model.base;

import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.obj.model.base.ObjBase;

public abstract class ObjVBase extends ObjBase {

	public ObjVBase(ObjVRepository repository, AggregateState state) {
		super(repository, state);
	}

}
