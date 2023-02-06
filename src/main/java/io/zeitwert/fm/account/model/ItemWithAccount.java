package io.zeitwert.fm.account.model;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.account.service.api.ObjAccountCache;

public interface ItemWithAccount {

	static ObjAccountCache getAccountCache() {
		return AppContext.getInstance().getBean(ObjAccountCache.class);
	}

	Integer getAccountId();

	void setAccountId(Integer accountId);

	ObjAccount getAccount();

}
