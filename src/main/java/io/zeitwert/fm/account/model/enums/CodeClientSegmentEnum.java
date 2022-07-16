
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeClientSegmentRecord;

@Component("codeClientSegmentEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeClientSegmentEnum extends EnumerationBase<CodeClientSegment> {

	private static CodeClientSegmentEnum INSTANCE;

	protected CodeClientSegmentEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeClientSegmentRecord item : this.getDslContext().selectFrom(Tables.CODE_CLIENT_SEGMENT).fetch()) {
			this.addItem(new CodeClientSegment(this, item.getId(), item.getName()));
		}
	}

	public static CodeClientSegment getClientSegment(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
