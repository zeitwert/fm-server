
package io.zeitwert.ddd.oe.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.ddd.oe.model.db.Tables;
import io.zeitwert.ddd.oe.model.db.tables.records.CodeUserRoleRecord;

@Component("codeUserRoleEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeUserRoleEnum extends EnumerationBase<CodeUserRole> {

	private static CodeUserRoleEnum INSTANCE;

	@Autowired
	protected CodeUserRoleEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeUserRoleRecord item : this.getDslContext().selectFrom(Tables.CODE_USER_ROLE).fetch()) {
			this.addItem(new CodeUserRole(this, item.getId(), item.getName()));
		}
	}

	public static CodeUserRole getUserRole(String userRoleId) {
		return INSTANCE.getItem(userRoleId);
	}

}
