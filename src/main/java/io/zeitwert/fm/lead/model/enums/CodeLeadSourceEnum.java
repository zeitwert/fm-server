
package io.zeitwert.fm.lead.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.lead.model.db.Tables;
import io.zeitwert.fm.lead.model.db.tables.records.CodeLeadSourceRecord;

@Component("codeLeadSourceEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeLeadSourceEnum extends EnumerationBase<CodeLeadSource> {

	private static CodeLeadSourceEnum INSTANCE;

	protected CodeLeadSourceEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeLeadSourceRecord item : this.getDslContext().selectFrom(Tables.CODE_LEAD_SOURCE).fetch()) {
			CodeLeadSource leadSource = CodeLeadSource.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(leadSource);
		}
	}

	public static CodeLeadSource getLeadSource(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
