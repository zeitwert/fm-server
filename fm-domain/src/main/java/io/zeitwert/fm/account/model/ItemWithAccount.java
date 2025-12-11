package io.zeitwert.fm.account.model;

public interface ItemWithAccount {

	Object getAccountId();

	void setAccountId(Object accountId);

	ObjAccount getAccount();

}
