
package io.zeitwert.ddd.property.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.aggregate.model.db.Tables;
import io.zeitwert.ddd.aggregate.model.db.tables.records.CodePartListTypeRecord;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codePartListTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodePartListTypeEnum extends EnumerationBase<CodePartListType> {

	private static CodePartListTypeEnum INSTANCE;

	protected CodePartListTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodePartListTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_PART_LIST_TYPE).fetch()) {
			this.addItem(new CodePartListType(this, item.getId(), item.getName()));
		}
	}

	public static CodePartListType getPartListType(String partListTypeId) {
		return INSTANCE.getItem(partListTypeId);
	}

}
