
package io.zeitwert.fm.oe.model;

import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.enums.CodeUserRole;
import io.zeitwert.fm.dms.model.ObjDocument;

import java.util.Set;

public interface ObjUserFM extends ObjUser {

	boolean isAppAdmin();

	boolean isAdmin();

	Boolean getNeedPasswordChange();

	void setNeedPasswordChange(Boolean needPasswordChange);

	String getPassword();

	void setPassword(String password);

	Integer getAvatarImageId();

	ObjDocument getAvatarImage();

	CodeUserRole getRole();

	boolean hasRole(CodeUserRole role);

	void setRole(CodeUserRole role);

	Set<ObjTenant> getTenantSet();

	void clearTenantSet();

	void addTenant(ObjTenant tenant);

	void removeTenant(ObjTenant tenant);

}
