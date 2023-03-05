
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeAccountTypeRecord;

@Component("codeAccountTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAccountTypeEnum extends JooqEnumerationBase<CodeAccountType> {

	private static CodeAccountTypeEnum INSTANCE;

	protected CodeAccountTypeEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeAccountType.class, appContext, dslContext);
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
