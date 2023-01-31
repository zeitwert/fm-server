
package io.zeitwert.ddd.aggregate.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Table;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeAggregateTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAggregateTypeEnum extends EnumerationBase<CodeAggregateType> {

	static private final String TABLE_NAME = "code_aggregate_type";

	private static CodeAggregateTypeEnum INSTANCE;

	protected CodeAggregateTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext, CodeAggregateType.class);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		Table<?> codeAggregateType = this.getDslContext().meta().getSchemas(AppContext.SCHEMA_NAME).get(0)
				.getTable(TABLE_NAME);
		for (final Record2<String, String> item : this.getDslContext().select(ID, NAME).from(codeAggregateType).fetch()) {
			CodeAggregateType aggregateType = CodeAggregateType.builder()
					.enumeration(this)
					.id(item.value1())
					.name(item.value2())
					.build();
			this.addItem(aggregateType);
		}
	}

	public static CodeAggregateType getAggregateType(String aggregateTypeId) {
		return INSTANCE.getItem(aggregateTypeId);
	}

}
