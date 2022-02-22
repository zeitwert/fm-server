package fm.comunas.ddd.common.model.enums;

import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.common.model.db.Tables;
import fm.comunas.ddd.common.model.db.tables.records.CodeLocaleRecord;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("codeLocaleEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeLocaleEnum extends EnumerationBase<CodeLocale> {

	private static CodeLocaleEnum INSTANCE;

	@Autowired
	protected CodeLocaleEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeLocaleRecord item : this.getDslContext().selectFrom(Tables.CODE_LOCALE).fetch()) {
			this.addItem(new CodeLocale(this, item.getId(), item.getName()));
		}
	}

	public static CodeLocale getLocale(String localeId) {
		return INSTANCE.getItem(localeId);
	}

}
