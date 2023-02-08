
package io.zeitwert.fm.dms.model.enums;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.CodeContentKindRecord;

@Component("codeContentKindEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeContentKindEnum extends EnumerationBase<CodeContentKind> {

	private static CodeContentKindEnum INSTANCE;

	protected CodeContentKindEnum(AppContext appContext) {
		super(appContext, CodeContentKind.class);
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
