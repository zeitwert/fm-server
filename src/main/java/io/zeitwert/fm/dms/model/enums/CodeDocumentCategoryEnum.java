
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeDocumentCategoryRecord;
import io.zeitwert.jooq.repository.JooqEnumerationBase;

@Component("codeDocumentCategoryEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeDocumentCategoryEnum extends JooqEnumerationBase<CodeDocumentCategory> {

	private static CodeDocumentCategoryEnum INSTANCE;

	protected CodeDocumentCategoryEnum(Enumerations enums, DSLContext dslContext) {
		super(CodeDocumentCategory.class, enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeDocumentCategoryRecord item : this.getDslContext().selectFrom(Tables.CODE_DOCUMENT_CATEGORY)
				.fetch()) {
			CodeDocumentCategory documentCategory = CodeDocumentCategory.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(documentCategory);
		}
	}

	public static CodeDocumentCategory getDocumentCategory(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
