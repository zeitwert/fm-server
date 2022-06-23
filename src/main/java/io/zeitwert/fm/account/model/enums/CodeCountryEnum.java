
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeCountryRecord;

@Component("codeCountryEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeCountryEnum extends EnumerationBase<CodeCountry> {

	private static CodeCountryEnum INSTANCE;

	protected CodeCountryEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeCountryRecord item : this.getDslContext().selectFrom(Tables.CODE_COUNTRY).fetch()) {
			this.addItem(new CodeCountry(this, item.getId(), item.getName()));
		}
	}

	public static CodeCountry getCountry(String countryId) {
		return INSTANCE.getItem(countryId);
	}

}
