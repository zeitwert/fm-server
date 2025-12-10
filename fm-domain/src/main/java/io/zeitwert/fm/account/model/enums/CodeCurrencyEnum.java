package io.zeitwert.fm.account.model.enums;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeCurrencyRecord;

@Component("codeCurrencyEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeCurrencyEnum extends JooqEnumerationBase<CodeCurrency> {

	private static CodeCurrencyEnum INSTANCE;

	protected CodeCurrencyEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeCurrency.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeCurrencyRecord item : this.getDslContext().selectFrom(Tables.CODE_CURRENCY).fetch()) {
			CodeCurrency currency = CodeCurrency.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(currency);
		}
	}

	public static CodeCurrency getCurrency(String currencyId) {
		return INSTANCE.getItem(currencyId);
	}

}
