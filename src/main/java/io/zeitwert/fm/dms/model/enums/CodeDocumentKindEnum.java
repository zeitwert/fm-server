
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.impl.Enumerations;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeDocumentKindRecord;

@Component("codeDocumentKindEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeDocumentKindEnum extends JooqEnumerationBase<CodeDocumentKind> {

	private static CodeDocumentKindEnum INSTANCE;

	protected CodeDocumentKindEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeDocumentKind.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeDocumentKindRecord item : this.getDslContext().selectFrom(Tables.CODE_DOCUMENT_KIND)
				.fetch()) {
			CodeDocumentKind documentKind = CodeDocumentKind.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(documentKind);
		}
	}

	public static CodeDocumentKind getDocumentKind(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
