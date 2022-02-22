
package fm.comunas.ddd.aggregate.model.enums;

import fm.comunas.ddd.aggregate.model.db.Tables;
import fm.comunas.ddd.aggregate.model.db.tables.records.CodeAggregateTypeRecord;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component("codeAggregateTypeEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeAggregateTypeEnum extends EnumerationBase<CodeAggregateType> {

	private static CodeAggregateTypeEnum INSTANCE;

	@Autowired
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
