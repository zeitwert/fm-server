
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.fm.contact.model.db.tables.records.CodeInteractionChannelRecord;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeInteractionChannelEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeInteractionChannelEnum extends EnumerationBase<CodeInteractionChannel> {

	private static CodeInteractionChannelEnum INSTANCE;

	@Autowired
	protected CodeInteractionChannelEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeInteractionChannelRecord item : this.getDslContext().selectFrom(Tables.CODE_INTERACTION_CHANNEL)
				.fetch()) {
			this.addItem(new CodeInteractionChannel(this, item.getId(), item.getName()));
		}
	}

	public static CodeInteractionChannel getInteractionChannel(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
