
package io.zeitwert.ddd.common.model.enums;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.common.model.db.Tables;
import io.zeitwert.ddd.common.model.db.tables.records.CodeCountryRecord;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("codeCountryEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeCountryEnum extends EnumerationBase<CodeCountry> {

	private static CodeCountryEnum INSTANCE;

	@Autowired
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
