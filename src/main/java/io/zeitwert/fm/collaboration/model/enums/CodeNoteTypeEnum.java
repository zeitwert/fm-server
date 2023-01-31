
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
		super(enums, dslContext, CodeNoteType.class);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeNoteTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_NOTE_TYPE).fetch()) {
			CodeNoteType noteType = CodeNoteType.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(noteType);
		}
	}

	public static CodeNoteType getNoteType(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
