package io.zeitwert.fm.obj.model.base;

import io.dddrive.obj.model.base.ObjBase;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.obj.model.ObjVRepository;

public abstract class ObjVBase extends ObjBase {

	//@formatter:off
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	//@formatter:on

	public ObjVBase(ObjVRepository repository, Object state) {
		super(repository, state);
	}

}
