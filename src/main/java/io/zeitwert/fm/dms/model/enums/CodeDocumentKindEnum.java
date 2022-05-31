
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.fm.dms.model.db.tables.records.CodeDocumentKindRecord;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.ddd.app.service.api.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeDocumentKindEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeDocumentKindEnum extends EnumerationBase<CodeDocumentKind> {

	private static CodeDocumentKindEnum INSTANCE;

	@Autowired
	protected CodeDocumentKindEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeDocumentKindRecord item : this.getDslContext().selectFrom(Tables.CODE_DOCUMENT_KIND)
				.fetch()) {
			this.addItem(new CodeDocumentKind(this, item.getId(), item.getName()));
		}
	}

	public static CodeDocumentKind getDocumentKind(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
