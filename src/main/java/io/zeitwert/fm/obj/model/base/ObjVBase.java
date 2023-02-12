package io.zeitwert.fm.obj.model.base;

import io.dddrive.obj.model.base.ObjBase;
import io.zeitwert.fm.obj.model.ObjVRepository;

public abstract class ObjVBase extends ObjBase {

	public ObjVBase(ObjVRepository repository, Object state) {
		super(repository, state);
	}

}
