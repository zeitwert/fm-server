
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeSalutationRecord;

@Component("codeSalutationEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeSalutationEnum extends JooqEnumerationBase<CodeSalutation> {

	private static CodeSalutationEnum INSTANCE;

	protected CodeSalutationEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeSalutation.class, appContext, dslContext);
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
