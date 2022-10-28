
package io.zeitwert.ddd.oe.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.ddd.oe.model.db.Tables;
import io.zeitwert.ddd.oe.model.db.tables.records.CodeUserRoleRecord;

@Component("codeUserRoleEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeUserRoleEnum extends EnumerationBase<CodeUserRole> {

	// normal user, needs account (so either in advisor or community tenant)
	public static CodeUserRole USER;
	// elevated user, needs account (so either in advisor or community tenant)
	public static CodeUserRole SUPER_USER;
	// read-only user, needs account (so either in advisor or community tenant)
	public static CodeUserRole READ_ONLY;

	// admin for a advisor or community tenant (1 tenant, n users, 1 .. n accounts)
	// login to advisor or community tenant, without account
	public static CodeUserRole ADMIN;
	// admin for a advisor or community tenant (tenants, users, accounts)
	// login to kernel tenant only, without account
	public static CodeUserRole APP_ADMIN;

	private static CodeUserRoleEnum INSTANCE;

	protected CodeUserRoleEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeUserRoleRecord item : this.getDslContext().selectFrom(Tables.CODE_USER_ROLE).fetch()) {
			this.addItem(new CodeUserRole(this, item.getId(), item.getName()));
		}
		USER = getUserRole("user");
		SUPER_USER = getUserRole("super_user");
		READ_ONLY = getUserRole("read_only");
		ADMIN = getUserRole("admin");
		APP_ADMIN = getUserRole("app_admin");
	}

	public static CodeUserRole getUserRole(String userRoleId) {
		return INSTANCE.getItem(userRoleId);
	}

}
