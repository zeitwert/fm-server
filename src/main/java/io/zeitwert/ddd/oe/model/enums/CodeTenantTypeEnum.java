
package io.zeitwert.ddd.oe.model.enums;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeTenantTypeEnum")
public class CodeTenantTypeEnum extends EnumerationBase<CodeTenantType> {

	// Kernel Tenant (Nr 1)
	// placeholder to do application administration (tenants, users)
	public static CodeTenantType KERNEL;
	// An Advisor Tenant may contain multiple Accounts
	public static CodeTenantType ADVISOR;
	// Container for 1 dedicated Account
	public static CodeTenantType COMMUNITY;

	static public final String TABLE_NAME = "code_tenant_type";

	private static CodeTenantTypeEnum INSTANCE;

	protected CodeTenantTypeEnum(Enumerations enums) {
		super(null, CodeTenantType.class);
		enums.addEnumeration(CodeTenantType.class, this);
		INSTANCE = this;
	}

	public static CodeTenantTypeEnum getInstance() {
		return INSTANCE;
	}

	public void addItem(CodeTenantType item) {
		super.addItem(item);
	}

	public void init() {
		KERNEL = getTenantType("kernel");
		ADVISOR = getTenantType("advisor");
		COMMUNITY = getTenantType("community");
	}

	public static CodeTenantType getTenantType(String tenantType) {
		return INSTANCE.getItem(tenantType);
	}

}
