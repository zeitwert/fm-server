package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeCurrencyRecord;

@Component("codeCurrencyEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeCurrencyEnum extends EnumerationBase<CodeCurrency> {

	private static CodeCurrencyEnum INSTANCE;

	protected CodeCurrencyEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeCurrencyRecord item : this.getDslContext().selectFrom(Tables.CODE_CURRENCY).fetch()) {
			this.addItem(new CodeCurrency(this, item.getId(), item.getName()));
		}
	}

	public static CodeCurrency getCurrency(String currencyId) {
		return INSTANCE.getItem(currencyId);
	}

}
