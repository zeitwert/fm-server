
package io.zeitwert.fm.oe.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.CodeTenantTypeRecord;

@Component("codeTenantTypeEnum")
public class CodeTenantTypeEnum extends JooqEnumerationBase<CodeTenantType> {

	// Kernel Tenant (Nr 1)
	// placeholder to do application administration (tenants, users)
	public static CodeTenantType KERNEL;
	// An Advisor Tenant may contain multiple Accounts
	public static CodeTenantType ADVISOR;
	// Container for 1 dedicated Account
	public static CodeTenantType COMMUNITY;

	private static CodeTenantTypeEnum INSTANCE;

	protected CodeTenantTypeEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeTenantType.class, enums, dslContext);
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
