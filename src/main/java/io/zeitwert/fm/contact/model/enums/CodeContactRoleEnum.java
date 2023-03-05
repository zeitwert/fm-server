
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeContactRoleRecord;

@Component("codeContactRoleEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeContactRoleEnum extends JooqEnumerationBase<CodeContactRole> {

	private static CodeContactRoleEnum INSTANCE;

	protected CodeContactRoleEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeContactRole.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeContactRoleRecord item : this.getDslContext().selectFrom(Tables.CODE_CONTACT_ROLE).fetch()) {
			CodeContactRole contactRole = CodeContactRole.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(contactRole);
		}
	}

	public static CodeContactRole getContactRole(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
