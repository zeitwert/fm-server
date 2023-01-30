
package io.zeitwert.ddd.part.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Table;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codePartListTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodePartListTypeEnum extends EnumerationBase<CodePartListType> {

	static private final String TABLE_NAME = "code_part_list_type";

	private static CodePartListTypeEnum INSTANCE;

	protected CodePartListTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		Table<?> codePartListType = this.getDslContext().meta().getSchemas(AppContext.SCHEMA_NAME).get(0)
				.getTable(TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID, NAME).from(codePartListType).fetch()) {
			CodePartListType partListType = CodePartListType.builder()
					.enumeration(this)
					.id(item.value1())
					.name(item.value2())
					.build();
			this.addItem(partListType);
		}
	}

	public static CodePartListType getPartListType(String partListTypeId) {
		return INSTANCE.getItem(partListTypeId);
	}

}
