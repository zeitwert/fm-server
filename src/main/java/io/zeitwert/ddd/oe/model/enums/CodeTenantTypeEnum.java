
package io.zeitwert.ddd.oe.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Table;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeTenantTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeTenantTypeEnum extends EnumerationBase<CodeTenantType> {

	// Kernel Tenant (Nr 1)
	// placeholder to do application administration (tenants, users)
	public static CodeTenantType KERNEL;
	// An Advisor Tenant may contain multiple Accounts
	public static CodeTenantType ADVISOR;
	// Container for 1 dedicated Account
	public static CodeTenantType COMMUNITY;

	static private final String TABLE_NAME = "code_tenant_type";

	private static CodeTenantTypeEnum INSTANCE;

	private final AppContext appContext;

	protected CodeTenantTypeEnum(final Enumerations enums, final DSLContext dslContext, final AppContext appContext) {
		super(enums, dslContext, CodeTenantType.class);
		this.appContext = appContext;
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		Table<?> codeTenantType = this.appContext.getTable(TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID, NAME).from(codeTenantType).fetch()) {
			CodeTenantType tenantType = CodeTenantType.builder()
					.enumeration(this)
					.id(item.value1())
					.name(item.value2())
					.build();
			this.addItem(tenantType);
		}
		KERNEL = getTenantType("kernel");
		ADVISOR = getTenantType("advisor");
		COMMUNITY = getTenantType("community");
	}

	public static CodeTenantType getTenantType(String tenantType) {
		return INSTANCE.getItem(tenantType);
	}

}
