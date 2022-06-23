
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeContentKindRecord;

@Component("codeContentKindEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeContentKindEnum extends EnumerationBase<CodeContentKind> {

	private static CodeContentKindEnum INSTANCE;

	protected CodeContentKindEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeContentKindRecord item : this.getDslContext().selectFrom(Tables.CODE_CONTENT_KIND).fetch()) {
			this.addItem(new CodeContentKind(this, item.getId(), item.getName()));
		}
	}

	public static CodeContentKind getContentKind(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
