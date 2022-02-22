
package fm.comunas.ddd.doc.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.doc.api.DocService;
import fm.comunas.ddd.doc.model.enums.CodeCaseStage;
import fm.comunas.ddd.doc.model.enums.CodeCaseStageEnum;

@Service("docService")
public class DocServiceImpl implements DocService {

	private static final String TABLE = "code_case_stage";
	private static final Table<?> CODE_CASE_STAGE = AppContext.getInstance().getSchema().getTable(TABLE);
	private static final Field<String> ID = DSL.field("id", String.class);
	private static final Field<String> CASE_DEF_ID = DSL.field("case_def_id", String.class);
	private static final Field<Integer> SEQ_NR = DSL.field("seq_nr", Integer.class);

	private final CodeCaseStageEnum caseStageEnum;
	private final DSLContext dslContext;

	@Autowired
	DocServiceImpl(final CodeCaseStageEnum caseStageEnum, final DSLContext dslContext) {
		this.caseStageEnum = caseStageEnum;
		this.dslContext = dslContext;
	}

	public List<CodeCaseStage> getCaseStages(String caseDefId) {
		//@formatter:off
		Result<Record1<String>> ids = this.dslContext
			.select(ID)
			.from(CODE_CASE_STAGE)
			.where(CASE_DEF_ID.eq(caseDefId))
			.orderBy(SEQ_NR)
			.fetch();
		//@formatter:on
		List<CodeCaseStage> stageList = new ArrayList<>();
		for (Record1<String> id : ids) {
			stageList.add(this.caseStageEnum.getItem(id.value1()));
		}
		return stageList;
	}

}
