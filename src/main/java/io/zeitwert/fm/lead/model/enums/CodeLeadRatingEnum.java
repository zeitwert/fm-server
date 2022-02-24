
package io.zeitwert.fm.lead.model.enums;

import io.zeitwert.fm.lead.model.db.Tables;
import io.zeitwert.fm.lead.model.db.tables.records.CodeLeadRatingRecord;
import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("codeLeadRatingEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeLeadRatingEnum extends EnumerationBase<CodeLeadRating> {

	private static CodeLeadRatingEnum INSTANCE;

	@Autowired
	protected CodeLeadRatingEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeLeadRatingRecord item : this.getDslContext().selectFrom(Tables.CODE_LEAD_RATING).fetch()) {
			this.addItem(new CodeLeadRating(this, item.getId(), item.getName()));
		}
	}

	public static CodeLeadRating getLeadRating(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
