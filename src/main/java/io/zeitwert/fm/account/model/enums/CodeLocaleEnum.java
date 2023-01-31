
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeLocaleRecord;

@Component("codeLocaleEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeLocaleEnum extends EnumerationBase<CodeLocale> {

	private static CodeLocaleEnum INSTANCE;

	protected CodeLocaleEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext, CodeLocale.class);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeLocaleRecord item : this.getDslContext().selectFrom(Tables.CODE_LOCALE).fetch()) {
			CodeLocale locale = CodeLocale.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(locale);
		}
	}

	public static CodeLocale getLocale(String localeId) {
		return INSTANCE.getItem(localeId);
	}

}
