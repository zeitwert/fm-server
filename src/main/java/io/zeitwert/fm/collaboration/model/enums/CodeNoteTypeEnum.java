
package io.zeitwert.fm.collaboration.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.CodeNoteTypeRecord;

@Component("codeNoteTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeNoteTypeEnum extends EnumerationBase<CodeNoteType> {

	private static CodeNoteTypeEnum INSTANCE;

	protected CodeNoteTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeNoteTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_NOTE_TYPE).fetch()) {
			this.addItem(new CodeNoteType(this, item.getId(), item.getName()));
		}
	}

	public static CodeNoteType getNoteType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
