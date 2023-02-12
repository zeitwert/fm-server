
package io.dddrive.oe.model;

import io.dddrive.obj.model.Obj;
import io.dddrive.oe.model.enums.CodeTenantType;

public interface ObjTenant extends Obj {

	CodeTenantType getTenantType();

	void setTenantType(CodeTenantType tenantType);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

}
