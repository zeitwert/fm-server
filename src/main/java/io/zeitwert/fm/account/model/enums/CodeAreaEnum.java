
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeAreaRecord;

@Component("codeAreaEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAreaEnum extends EnumerationBase<CodeArea> {

	private static CodeAreaEnum INSTANCE;

	protected CodeAreaEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAreaRecord item : this.getDslContext().selectFrom(Tables.CODE_AREA).fetch()) {
			CodeArea area = CodeArea.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(area);
		}
	}

	public static CodeArea getArea(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
