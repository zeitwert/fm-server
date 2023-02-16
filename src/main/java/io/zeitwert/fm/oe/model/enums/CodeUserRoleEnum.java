
package io.zeitwert.fm.oe.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.CodeUserRoleRecord;

@Component("codeUserRoleEnum")
public class CodeUserRoleEnum extends JooqEnumerationBase<CodeUserRole> {

	// application admin (tenants, users, accounts)
	// login to kernel tenant only, without account
	public static CodeUserRole APP_ADMIN;
	// admin for a advisor or community tenant (1 tenant, n users, 1 .. n accounts)
	// login to advisor or community tenant, without account
	public static CodeUserRole ADMIN;

	// normal user, needs account (so either in advisor or community tenant)
	public static CodeUserRole USER;
	// elevated user, needs account (so either in advisor or community tenant)
	public static CodeUserRole SUPER_USER;
	// read-only user, needs account (so either in advisor or community tenant)
	public static CodeUserRole READ_ONLY;

	private static CodeUserRoleEnum INSTANCE;

	protected CodeUserRoleEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeUserRole.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeUserRoleRecord item : this.getDslContext().selectFrom(Tables.CODE_USER_ROLE).fetch()) {
			CodeUserRole userRole = CodeUserRole.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(userRole);
		}
		APP_ADMIN = getUserRole("app_admin");
		ADMIN = getUserRole("admin");
		USER = getUserRole("user");
		SUPER_USER = getUserRole("super_user");
		READ_ONLY = getUserRole("read_only");
	}

	public static CodeUserRole getUserRole(String userRoleId) {
		return INSTANCE.getItem(userRoleId);
	}

}
