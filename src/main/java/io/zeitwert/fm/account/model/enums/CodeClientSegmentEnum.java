
package io.zeitwert.fm.account.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.CodeClientSegmentRecord;

@Component("codeClientSegmentEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeClientSegmentEnum extends EnumerationBase<CodeClientSegment> {

	private static CodeClientSegmentEnum INSTANCE;

	protected CodeClientSegmentEnum(AppContext appContext) {
		super(appContext, CodeClientSegment.class);
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
