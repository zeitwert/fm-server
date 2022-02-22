
package fm.comunas.ddd.session.model;

import fm.comunas.ddd.common.model.enums.CodeLocale;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjUser;

import java.util.HashMap;
import java.util.Map;

public class SessionInfo {

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

}
