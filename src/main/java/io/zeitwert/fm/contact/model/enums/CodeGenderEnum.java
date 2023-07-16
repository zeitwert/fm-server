
package io.zeitwert.fm.contact.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.CodeGenderRecord;

@Component("codeGenderEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeGenderEnum extends JooqEnumerationBase<CodeGender> {

	private static CodeGenderEnum INSTANCE;

	protected CodeGenderEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeGender.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeGenderRecord item : this.getDslContext().selectFrom(Tables.CODE_GENDER).fetch()) {
			CodeGender gender = CodeGender.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(gender);
		}
	}

	public static CodeGender getGender(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
