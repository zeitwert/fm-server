
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;
import io.zeitwert.fm.dms.model.ObjDocument;

import java.util.List;

public interface ObjTenant extends Obj {

	CodeTenantType getTenantType();

	void setTenantType(CodeTenantType tenantType);

	String getExtlKey();

	void setExtlKey(String extlKey);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	Integer getLogoImageId();

	ObjDocument getLogoImage();

	Integer getBannerImageId();

	ObjDocument getBannerImage();

	List<ObjUser> getUsers();

}
