
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeSalutationRecord;

@Component("codeSalutationEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeSalutationEnum extends EnumerationBase<CodeSalutation> {

	private static CodeSalutationEnum INSTANCE;

	protected CodeSalutationEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeSalutationRecord item : this.getDslContext().selectFrom(Tables.CODE_SALUTATION).fetch()) {
			CodeSalutation salutation = CodeSalutation.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(salutation);
		}
	}

	public static CodeSalutation getSalutation(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
