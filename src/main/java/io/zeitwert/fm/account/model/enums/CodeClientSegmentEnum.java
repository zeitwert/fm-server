
package io.zeitwert.fm.account.model.enums;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeClientSegmentRecord;

@Component("codeClientSegmentEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeClientSegmentEnum extends JooqEnumerationBase<CodeClientSegment> {

	private static CodeClientSegmentEnum INSTANCE;

	protected CodeClientSegmentEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeClientSegment.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeClientSegmentRecord item : this.getDslContext().selectFrom(Tables.CODE_CLIENT_SEGMENT).fetch()) {
			CodeClientSegment segment = CodeClientSegment.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(segment);
		}
	}

	public static CodeClientSegment getClientSegment(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
