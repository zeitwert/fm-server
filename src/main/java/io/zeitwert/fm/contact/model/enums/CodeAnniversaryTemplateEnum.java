
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.fm.contact.model.db.tables.records.CodeAnniversaryTemplateRecord;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeAnniversaryTemplateEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAnniversaryTemplateEnum extends EnumerationBase<CodeAnniversaryTemplate> {

	private static CodeAnniversaryTemplateEnum INSTANCE;

	@Autowired
	protected CodeAnniversaryTemplateEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAnniversaryTemplateRecord item : this.getDslContext().selectFrom(Tables.CODE_ANNIVERSARY_TEMPLATE)
				.fetch()) {
			this.addItem(new CodeAnniversaryTemplate(this, item.getId(), item.getName()));
		}
	}

	public static CodeAnniversaryTemplate getAnniversaryTemplate(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
