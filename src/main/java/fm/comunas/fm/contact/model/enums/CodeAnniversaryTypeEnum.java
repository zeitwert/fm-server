
package fm.comunas.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.fm.contact.model.db.tables.records.CodeAnniversaryTypeRecord;
import fm.comunas.fm.contact.model.db.Tables;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

@Component("codeAnniversaryTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAnniversaryTypeEnum extends EnumerationBase<CodeAnniversaryType> {

	private static CodeAnniversaryTypeEnum INSTANCE;

	@Autowired
	protected CodeAnniversaryTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAnniversaryTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_ANNIVERSARY_TYPE).fetch()) {
			this.addItem(new CodeAnniversaryType(this, item.getId(), item.getName()));
		}
	}

	public static CodeAnniversaryType getAnniversaryType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
