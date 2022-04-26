
package io.zeitwert.ddd.session.model;

import io.zeitwert.ddd.common.model.enums.CodeLocale;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;

import java.util.HashMap;
import java.util.Map;

public class SessionInfo {

	public final String ACCOUNT = "account";

	private final ObjTenant tenant;
	private final ObjUser user;
	private final CodeLocale locale;
	private final Map<String, Object> customValues = new HashMap<>();

	public SessionInfo(ObjTenant tenant, ObjUser user, CodeLocale locale) {
		this.tenant = tenant;
		this.user = user;
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

	public CodeLocale getLocale() {
		return this.locale;
	}

	public boolean hasAccount() {
		return this.hasCustomValue(ACCOUNT);
	}

	@SuppressWarnings("unchecked")
	public Integer getAccountId() {
		return Integer.parseInt(((Map<String, Object>) this.getCustomValue(ACCOUNT)).get("id").toString());
	}

	public boolean hasCustomValue(String key) {
		return this.customValues.get(key) != null;
	}

	public Object getCustomValue(String key) {
		return this.customValues.get(key);
	}

	public Map<String, Object> getCustomValues() {
		return Map.copyOf(this.customValues);
	}

	public void clearCustomValues() {
		this.customValues.clear();
	}

	public void setCustomValue(String key, Object value) {
		this.customValues.put(key, value);
	}

	@Override
	public String toString() {
		return "SessionInfo("
				+ "tenant: " + (tenant != null ? tenant.getId() : "null")
				+ ", user: " + (user != null ? user.getId() : "null")
				+ ", locale: " + (locale != null ? locale.getId() : "null")
				+ ", customValues: " + customValues
				+ ")";
	}

}
