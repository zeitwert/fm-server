
package io.zeitwert.fm.lead.model.enums;

import io.zeitwert.fm.lead.model.db.Tables;
import io.zeitwert.fm.lead.model.db.tables.records.CodeLeadSourceRecord;
import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("codeLeadSourceEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeLeadSourceEnum extends EnumerationBase<CodeLeadSource> {

	private static CodeLeadSourceEnum INSTANCE;

	@Autowired
	protected CodeLeadSourceEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeLeadSourceRecord item : this.getDslContext().selectFrom(Tables.CODE_LEAD_SOURCE).fetch()) {
			this.addItem(new CodeLeadSource(this, item.getId(), item.getName()));
		}
	}

	public static CodeLeadSource getLeadSource(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
