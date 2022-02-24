
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeAccountTypeRecord;

@Component("codeAccountTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAccountTypeEnum extends EnumerationBase<CodeAccountType> {

	private static CodeAccountTypeEnum INSTANCE;

	@Autowired
	protected CodeAccountTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAccountTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_ACCOUNT_TYPE).fetch()) {
			this.addItem(new CodeAccountType(this, item.getId(), item.getName()));
		}
	}

	public static CodeAccountType getAccountType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
