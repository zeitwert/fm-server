
package fm.comunas.ddd.doc.model.enums;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record9;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.app.service.api.Enumerations;
import fm.comunas.ddd.enums.model.base.EnumerationBase;

@Component("codeCaseStageEnum")
@DependsOn({ "flyway", "flywayInitializer" })
public class CodeCaseStageEnum extends EnumerationBase<CodeCaseStage> {

	static private final String TABLE = "code_case_stage";

	static protected final Field<String> CASE_DEF_ID = DSL.field("case_def_id", String.class);
	static protected final Field<String> CASE_STAGE_TYPE_ID = DSL.field("case_stage_type_id", String.class);
	static protected final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static protected final Field<Integer> SEQ_NR = DSL.field("seq_nr", Integer.class);
	static protected final Field<String> ABSTRACT_CASE_STAGE_ID = DSL.field("abstract_case_stage_id", String.class);
	static protected final Field<String> ACTION = DSL.field("action", String.class);
	static protected final Field<String> AVAILABLE_ACTIONS = DSL.field("available_actions", String.class);

	@Autowired
	protected CodeCaseStageEnum(final Enumerations enums, final DSLContext dslContext) {
		super(enums, dslContext);
	}

	@PostConstruct
	private void init() {
		Table<?> codeCaseStage = AppContext.getInstance().getSchema().getTable(TABLE);
		for (final Record9<String, String, String, String, String, Integer, String, String, String> item : this
				.getDslContext().select(ID, NAME, CASE_DEF_ID, CASE_STAGE_TYPE_ID, DESCRIPTION, SEQ_NR, ABSTRACT_CASE_STAGE_ID,
						ACTION, AVAILABLE_ACTIONS)
				.from(codeCaseStage).fetch()) {
			this.addItem(new CodeCaseStage(this, item.value1(), item.value3(), item.value4(), item.value2(), item.value5(),
					item.value6(), item.value7(), item.value8(), item.value9()));
		}
	}

}
