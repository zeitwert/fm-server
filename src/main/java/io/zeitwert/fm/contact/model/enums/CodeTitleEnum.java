
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeTitleRecord;

@Component("codeTitleEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeTitleEnum extends EnumerationBase<CodeTitle> {

	private static CodeTitleEnum INSTANCE;

	protected CodeTitleEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeTitleRecord item : this.getDslContext().selectFrom(Tables.CODE_TITLE).fetch()) {
			this.addItem(new CodeTitle(this, item.getId(), item.getName()));
		}
	}

	public static CodeTitle getTitle(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
