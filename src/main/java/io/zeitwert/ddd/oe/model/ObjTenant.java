
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.dms.model.ObjDocument;

import java.math.BigDecimal;
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

	List<ObjUser> getUsers();

	List<ObjAccount> getAccounts();

	BigDecimal getInflationRate();

	void setInflationRate(BigDecimal rate);

	Integer getLogoImageId();

	ObjDocument getLogoImage();

}
