
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

	static private final String TABLE_NAME = "code_user_role";

	private static CodeUserRoleEnum INSTANCE;

	private final AppContext appContext;

	protected CodeUserRoleEnum(final Enumerations enums, final DSLContext dslContext, final AppContext appContext) {
		super(enums, dslContext, CodeUserRole.class);
		this.appContext = appContext;
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		Table<?> codeUserRole = this.appContext.getTable(TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID, NAME).from(codeUserRole).fetch()) {
			CodeUserRole userRole = CodeUserRole.builder()
					.enumeration(this)
					.id(item.value1())
					.name(item.value2())
					.build();
			this.addItem(userRole);
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
