
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeGenderRecord;

@Component("codeGenderEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeGenderEnum extends EnumerationBase<CodeGender> {

	private static CodeGenderEnum INSTANCE;

	protected CodeGenderEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeGenderRecord item : this.getDslContext().selectFrom(Tables.CODE_GENDER).fetch()) {
			this.addItem(new CodeGender(this, item.getId(), item.getName()));
		}
	}

	public static CodeGender getGender(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
