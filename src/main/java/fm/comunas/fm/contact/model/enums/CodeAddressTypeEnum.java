
package fm.comunas.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.fm.contact.model.db.tables.records.CodeAddressTypeRecord;
import fm.comunas.fm.contact.model.db.Tables;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

@Component("codeAddressTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAddressTypeEnum extends EnumerationBase<CodeAddressType> {

	private static CodeAddressTypeEnum INSTANCE;

	@Autowired
	protected CodeAddressTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAddressTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_ADDRESS_TYPE).fetch()) {
			this.addItem(new CodeAddressType(this, item.getId(), item.getName()));
		}
	}

	public static CodeAddressType getAddressType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
