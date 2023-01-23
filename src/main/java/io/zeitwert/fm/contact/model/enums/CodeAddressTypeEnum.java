
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeAddressTypeRecord;

@Component("codeAddressTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAddressTypeEnum extends EnumerationBase<CodeAddressType> {

	private static CodeAddressTypeEnum INSTANCE;

	protected CodeAddressTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAddressTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_ADDRESS_TYPE).fetch()) {
			CodeAddressType addressType = CodeAddressType.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(addressType);
		}
	}

	public static CodeAddressType getAddressType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
