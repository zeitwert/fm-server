package io.zeitwert.fm.oe.model;

import io.dddrive.core.oe.model.ObjTenant;
import io.dddrive.core.oe.model.ObjUser;
import io.zeitwert.fm.oe.model.enums.CodeUserRole;
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// import io.zeitwert.fm.dms.model.ObjDocument;

import java.util.Set;

public interface ObjUserFM extends ObjUser {

	boolean isAppAdmin();

	boolean isAdmin();

	Boolean getNeedPasswordChange();

	void setNeedPasswordChange(Boolean needPasswordChange);

	String getPassword();

	void setPassword(String password);

	// TODO-MIGRATION: DMS - uncomment after DMS is migrated
	// Integer getAvatarImageId();

	// TODO-MIGRATION: DMS - uncomment after DMS is migrated
	// ObjDocument getAvatarImage();

	CodeUserRole getRole();

	boolean hasRole(CodeUserRole role);

	void setRole(CodeUserRole role);

	Set<ObjTenant> getTenantSet();

	void clearTenantSet();

	void addTenant(ObjTenant tenant);

	void removeTenant(ObjTenant tenant);

}
