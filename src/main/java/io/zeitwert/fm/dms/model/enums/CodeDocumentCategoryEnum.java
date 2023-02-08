
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeDocumentCategoryRecord;

@Component("codeDocumentCategoryEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeDocumentCategoryEnum extends EnumerationBase<CodeDocumentCategory> {

	private static CodeDocumentCategoryEnum INSTANCE;

	protected CodeDocumentCategoryEnum(AppContext appContext) {
		super(appContext, CodeDocumentCategory.class);
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
