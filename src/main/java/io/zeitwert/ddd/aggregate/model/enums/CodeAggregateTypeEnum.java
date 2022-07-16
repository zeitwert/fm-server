
package io.zeitwert.ddd.aggregate.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.aggregate.model.db.Tables;
import io.zeitwert.ddd.aggregate.model.db.tables.records.CodeAggregateTypeRecord;
import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeAggregateTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAggregateTypeEnum extends EnumerationBase<CodeAggregateType> {

	private static CodeAggregateTypeEnum INSTANCE;

	protected CodeAggregateTypeEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
		INSTANCE = this;
	}

	@PostConstruct
	private void init() {
		for (final CodeAggregateTypeRecord item : this.getDslContext().selectFrom(Tables.CODE_AGGREGATE_TYPE).fetch()) {
			this.addItem(new CodeAggregateType(this, item.getId(), item.getName()));
		}
	}

	public static CodeAggregateType getAggregateType(String aggregateTypeId) {
		return INSTANCE.getItem(aggregateTypeId);
	}

}
