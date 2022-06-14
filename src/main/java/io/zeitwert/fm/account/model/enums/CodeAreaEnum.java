
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeAreaRecord;
import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeAreaEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAreaEnum extends EnumerationBase<CodeArea> {

	private static CodeAreaEnum INSTANCE;

	@Autowired
	protected CodeAreaEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAreaRecord item : this.getDslContext().selectFrom(Tables.CODE_AREA).fetch()) {
			this.addItem(new CodeArea(this, item.getId(), item.getName()));
		}
	}

	public static CodeArea getArea(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
