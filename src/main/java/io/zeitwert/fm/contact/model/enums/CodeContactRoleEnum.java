
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeContactRoleRecord;
import io.zeitwert.jooq.repository.JooqEnumerationBase;

@Component("codeContactRoleEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeContactRoleEnum extends JooqEnumerationBase<CodeContactRole> {

	private static CodeContactRoleEnum INSTANCE;

	protected CodeContactRoleEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeContactRole.class, enums, dslContext);
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
