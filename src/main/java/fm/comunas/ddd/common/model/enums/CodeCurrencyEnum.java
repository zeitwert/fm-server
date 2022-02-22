package fm.comunas.ddd.common.model.enums;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.common.model.db.Tables;
import fm.comunas.ddd.common.model.db.tables.records.CodeCurrencyRecord;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("codeCurrencyEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeCurrencyEnum extends EnumerationBase<CodeCurrency> {

	private static CodeCurrencyEnum INSTANCE;

	@Autowired
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
