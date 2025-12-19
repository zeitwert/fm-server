package io.zeitwert.fm.account.model

interface ItemWithAccount {

	var accountId: Any?

	val account: ObjAccount?

}
