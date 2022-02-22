
package fm.comunas.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.fm.contact.model.db.Tables;
import fm.comunas.fm.contact.model.db.tables.records.CodeGenderRecord;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

@Component("codeGenderEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeGenderEnum extends EnumerationBase<CodeGender> {

	private static CodeGenderEnum INSTANCE;

	@Autowired
	protected CodeGenderEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeGenderRecord item : this.getDslContext().selectFrom(Tables.CODE_GENDER).fetch()) {
			this.addItem(new CodeGender(this, item.getId(), item.getName()));
		}
	}

	public static CodeGender getGender(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
