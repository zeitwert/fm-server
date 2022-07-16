
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeAddressChannelRecord;

@Component("codeInteractionChannelEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAddressChannelEnum extends EnumerationBase<CodeAddressChannel> {

	private static CodeAddressChannelEnum INSTANCE;

	protected CodeAddressChannelEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAddressChannelRecord item : this.getDslContext().selectFrom(Tables.CODE_ADDRESS_CHANNEL)
				.fetch()) {
			this.addItem(new CodeAddressChannel(this, item.getId(), item.getName()));
		}
	}

	public static CodeAddressChannel getAddressChannel(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
