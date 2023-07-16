
package io.zeitwert.fm.oe.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.CodeCountryRecord;

@Component("codeCountryEnum")
public class CodeCountryEnum extends JooqEnumerationBase<CodeCountry> {

	private static CodeCountryEnum INSTANCE;

	protected CodeCountryEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeCountry.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeCountryRecord item : this.getDslContext().selectFrom(Tables.CODE_COUNTRY).fetch()) {
			CodeCountry country = CodeCountry.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(country);
		}
	}

	public static CodeCountry getCountry(String countryId) {
		return INSTANCE.getItem(countryId);
	}

}
