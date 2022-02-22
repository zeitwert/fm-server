
package fm.comunas.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.fm.contact.model.db.Tables;
import fm.comunas.fm.contact.model.db.tables.records.CodeSalutationRecord;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

@Component("codeSalutationEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeSalutationEnum extends EnumerationBase<CodeSalutation> {

	private static CodeSalutationEnum INSTANCE;

	@Autowired
	protected CodeSalutationEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeSalutationRecord item : this.getDslContext().selectFrom(Tables.CODE_SALUTATION).fetch()) {
			this.addItem(new CodeSalutation(this, item.getId(), item.getName()));
		}
	}

	public static CodeSalutation getSalutation(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
