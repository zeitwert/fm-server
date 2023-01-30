
package io.zeitwert.ddd.doc.service.api.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.doc.service.api.DocService;

@Service("docService")
@DependsOn("appContext")
public class DocServiceImpl implements DocService {

	static private final String CASE_STAGE_TABLE_NAME = "code_case_stage";
	static private final Table<?> CASE_STAGE_TABLE = AppContext.getInstance().getTable(CASE_STAGE_TABLE_NAME);

	static private final Field<String> ID = DSL.field("id", String.class);
	static private final Field<String> CASE_DEF_ID = DSL.field("case_def_id", String.class);
	static private final Field<Integer> SEQ_NR = DSL.field("seq_nr", Integer.class);

	private final DSLContext dslContext;

	DocServiceImpl(final CodeCaseStageEnum caseStageEnum, final DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	@Override
	public List<CodeCaseStage> getCaseStages(String caseDefId) {
		Result<Record1<String>> ids = this.dslContext
				.select(ID)
				.from(CASE_STAGE_TABLE)
				.where(CASE_DEF_ID.eq(caseDefId))
				.orderBy(SEQ_NR)
				.fetch();
		return ids.stream().map(id -> CodeCaseStageEnum.getCaseStage(id.value1())).toList();
	}

}
