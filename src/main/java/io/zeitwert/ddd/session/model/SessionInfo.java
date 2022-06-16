
package io.zeitwert.ddd.session.model;

import io.zeitwert.fm.account.model.enums.CodeLocale;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;

public class SessionInfo {

	private final ObjTenant tenant;
	private final ObjUser user;
	private final Integer accountId;
	private final CodeLocale locale;

	public SessionInfo(ObjTenant tenant, ObjUser user, Integer accountId, CodeLocale locale) {
		this.tenant = tenant;
		this.user = user;
		this.accountId = accountId;
		this.locale = locale;
	}

	public Integer getId() {
		return System.identityHashCode(this);
	}

	public ObjTenant getTenant() {
		return this.tenant;
	}

	public ObjUser getUser() {
		return this.user;
	}

	public boolean hasAccount() {
		return this.accountId != null;
	}

	public Integer getAccountId() {
		return this.accountId;
	}

	public CodeLocale getLocale() {
		return this.locale;
	}

	@Override
	public String toString() {
		return "SessionInfo("
				+ "tenant: " + (tenant != null ? tenant.getId() : "null")
				+ ", user: " + (user != null ? user.getId() : "null")
				+ ", accountId: " + accountId
				+ ", locale: " + (locale != null ? locale.getId() : "null")
				+ ")";
	}

}
