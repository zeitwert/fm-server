
package io.zeitwert.fm.oe.model.enums;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.CodeLocaleRecord;

@Component("codeLocaleEnum")
public class CodeLocaleEnum extends JooqEnumerationBase<CodeLocale> {

	private static CodeLocaleEnum INSTANCE;

	protected CodeLocaleEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeLocale.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeLocaleRecord item : this.getDslContext().selectFrom(Tables.CODE_LOCALE).fetch()) {
			CodeLocale country = CodeLocale.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(country);
		}
	}

	public static CodeLocale getLocale(String localeId) {
		return INSTANCE.getItem(localeId);
	}

}
