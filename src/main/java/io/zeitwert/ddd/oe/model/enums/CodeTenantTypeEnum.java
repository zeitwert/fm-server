
package io.zeitwert.ddd.oe.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.ddd.oe.model.db.Tables;
import io.zeitwert.ddd.oe.model.db.tables.records.CodeTenantTypeRecord;

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

	private static CodeTenantTypeEnum INSTANCE;

	protected CodeTenantTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeTenantTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_TENANT_TYPE).fetch()) {
			CodeTenantType tenantType = CodeTenantType.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
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
