package io.zeitwert.fm.account.model.impl;

import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.obj.model.base.ObjPropertyProviderBase;
import io.zeitwert.fm.account.model.ObjAccount;

@Configuration("accountPropertyProvider")
public class ObjAccountPropertyProvider extends ObjPropertyProviderBase {

	@Override
	public Class<?> getEntityClass() {
		return ObjAccount.class;
	}

}
