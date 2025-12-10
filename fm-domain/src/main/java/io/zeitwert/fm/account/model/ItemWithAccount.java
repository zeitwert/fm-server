package io.zeitwert.fm.account.model;

public interface ItemWithAccount {

	Integer getAccountId();

	void setAccountId(Integer accountId);

	ObjAccount getAccount();

}
