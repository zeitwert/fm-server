
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;

public interface ObjTenant extends Obj {

	CodeTenantType getTenantType();

	void setTenantType(CodeTenantType tenantType);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

}
