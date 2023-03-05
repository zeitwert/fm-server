
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.jooq.enums.JooqEnumerationBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeContentKindRecord;

@Component("codeContentKindEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeContentKindEnum extends JooqEnumerationBase<CodeContentKind> {

	private static CodeContentKindEnum INSTANCE;

	protected CodeContentKindEnum(AppContextSPI appContext, DSLContext dslContext) {
		super(CodeContentKind.class, appContext, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeContentKindRecord item : this.getDslContext().selectFrom(Tables.CODE_CONTENT_KIND).fetch()) {
			CodeContentKind contentKind = CodeContentKind.builder()
					.enumeration(this)
					.id(item.getId())
					.name(item.getName())
					.build();
			this.addItem(contentKind);
		}
	}

	public static CodeContentKind getContentKind(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
