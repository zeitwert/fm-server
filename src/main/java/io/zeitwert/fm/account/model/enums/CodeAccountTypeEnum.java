
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeAccountTypeRecord;
import io.zeitwert.jooq.repository.JooqEnumerationBase;

@Component("codeAccountTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAccountTypeEnum extends JooqEnumerationBase<CodeAccountType> {

	private static CodeAccountTypeEnum INSTANCE;

	protected CodeAccountTypeEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeAccountType.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAccountTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_ACCOUNT_TYPE).fetch()) {
			CodeAccountType accountType = CodeAccountType.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(accountType);
		}
	}

	public static CodeAccountType getAccountType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
