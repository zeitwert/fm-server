package io.zeitwert.fm.obj.model.base;

import io.zeitwert.ddd.obj.model.base.ObjPropertyProviderBase;

public class FMObjPropertyProviderBase extends ObjPropertyProviderBase {

	public FMObjPropertyProviderBase() {
		super();
		this.mapField("account", DbTableType.BASE, "account_id", Integer.class);
	}

}
