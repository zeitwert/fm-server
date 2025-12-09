
package io.zeitwert.fm.collaboration.model.enums;

import jakarta.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.CodeNoteTypeRecord;

@Component("codeNoteTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeNoteTypeEnum extends JooqEnumerationBase<CodeNoteType> {

	private static CodeNoteTypeEnum INSTANCE;

	protected CodeNoteTypeEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeNoteType.class, appContext, dslContext);
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
