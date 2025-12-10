package io.zeitwert.fm.oe.model;

import io.dddrive.core.oe.model.ObjTenant;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.oe.model.enums.CodeTenantType;

import java.math.BigDecimal;
import java.util.List;

public interface ObjTenantFM extends ObjTenant {

	CodeTenantType getTenantType();

	void setTenantType(CodeTenantType tenantType);

	BigDecimal getInflationRate();

	void setInflationRate(BigDecimal rate);

	BigDecimal getDiscountRate();

	void setDiscountRate(BigDecimal rate);

	List<ObjUserFM> getUsers();

	Integer getLogoImageId();

	ObjDocument getLogoImage();

}
