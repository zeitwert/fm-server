package io.zeitwert.fm.obj.model.base;

import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;

public abstract class ObjVBase extends ObjBase {

	public ObjVBase(ObjVRepository repository, Object state) {
		super(repository, state);
	}

}
