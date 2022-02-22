
package fm.comunas.fm.common.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.fm.common.model.db.Tables;
import fm.comunas.fm.common.model.db.tables.records.CodeAreaRecord;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

@Component("codeAreaEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAreaEnum extends EnumerationBase<CodeArea> {

	private static CodeAreaEnum INSTANCE;

	@Autowired
	protected CodeAreaEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAreaRecord item : this.getDslContext().selectFrom(Tables.CODE_AREA).fetch()) {
			this.addItem(new CodeArea(this, item.getId(), item.getName()));
		}
	}

	public static CodeArea getArea(String itemId) {
		return INSTANCE.getItem(itemId);
	}

}
