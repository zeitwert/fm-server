
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeCountryRecord;

@Component("codeCountryEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeCountryEnum extends EnumerationBase<CodeCountry> {

	private static CodeCountryEnum INSTANCE;

	protected CodeCountryEnum(AppContext appContext) {
		super(appContext, CodeCountry.class);
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
