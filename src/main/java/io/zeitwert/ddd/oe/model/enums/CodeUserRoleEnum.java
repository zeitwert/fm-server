
package io.zeitwert.ddd.oe.model.enums;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeUserRoleEnum")
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

	static public final String TABLE_NAME = "code_user_role";

	private static CodeUserRoleEnum INSTANCE;

	protected CodeUserRoleEnum(Enumerations enums) {
		super(null, CodeUserRole.class);
		enums.addEnumeration(CodeUserRole.class, this);
		INSTANCE = this;
	}

	public static CodeUserRoleEnum getInstance() {
		return INSTANCE;
	}

	public void addItem(CodeUserRole item) {
		super.addItem(item);
	}

	public void init() {
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
