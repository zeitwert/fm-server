
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeAddressChannelRecord;

@Component("codeInteractionChannelEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAddressChannelEnum extends EnumerationBase<CodeAddressChannel> {

	private static CodeAddressChannelEnum INSTANCE;

	protected CodeAddressChannelEnum(AppContext appContext) {
		super(appContext, CodeAddressChannel.class);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAddressChannelRecord item : this.getDslContext().selectFrom(Tables.CODE_ADDRESS_CHANNEL)
				.fetch()) {
			CodeAddressChannel channel = CodeAddressChannel.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(channel);
		}
	}

	public static CodeAddressChannel getAddressChannel(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
