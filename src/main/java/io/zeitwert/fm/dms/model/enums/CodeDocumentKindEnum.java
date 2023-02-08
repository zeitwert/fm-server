
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeDocumentKindRecord;

@Component("codeDocumentKindEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeDocumentKindEnum extends EnumerationBase<CodeDocumentKind> {

	private static CodeDocumentKindEnum INSTANCE;

	protected CodeDocumentKindEnum(AppContext appContext) {
		super(appContext, CodeDocumentKind.class);
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
