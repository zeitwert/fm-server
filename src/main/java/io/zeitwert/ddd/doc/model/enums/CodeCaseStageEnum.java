
package io.zeitwert.ddd.doc.model.enums;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.impl.Enumerations;
import io.zeitwert.ddd.enums.model.base.EnumerationBase;

@Component("codeCaseStageEnum")
public class CodeCaseStageEnum extends EnumerationBase<CodeCaseStage> {

	static public final String TABLE_NAME = "code_case_stage";

	static public final Field<String> CASE_DEF_ID = DSL.field("case_def_id", String.class);
	static public final Field<String> CASE_STAGE_TYPE_ID = DSL.field("case_stage_type_id", String.class);
	static public final Field<String> DESCRIPTION = DSL.field("description", String.class);
	static public final Field<Integer> SEQ_NR = DSL.field("seq_nr", Integer.class);
	static public final Field<String> ABSTRACT_CASE_STAGE_ID = DSL.field("abstract_case_stage_id", String.class);
	static public final Field<String> ACTION = DSL.field("action", String.class);
	static public final Field<String> AVAILABLE_ACTIONS = DSL.field("available_actions", String.class);

	static private CodeCaseStageEnum INSTANCE;

	protected CodeCaseStageEnum(Enumerations enums) {
		super(null, CodeCaseStage.class);
		enums.addEnumeration(CodeCaseStage.class, this);
		INSTANCE = this;
	}

	public static CodeCaseStageEnum getInstance() {
		return INSTANCE;
	}

	public void addItem(CodeCaseStage item) {
		super.addItem(item);
	}

	public static CodeCaseStage getCaseStage(String caseStageId) {
		return INSTANCE.getItem(caseStageId);
	}

}
