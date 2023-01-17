
package io.zeitwert.ddd.doc.service.api.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.doc.model.db.Tables;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.doc.service.api.DocService;

@Service("docService")
public class DocServiceImpl implements DocService {

	private final DSLContext dslContext;

	DocServiceImpl(final CodeCaseStageEnum caseStageEnum, final DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	@Override
	public List<CodeCaseStage> getCaseStages(Integer docId) {
		Result<Record1<String>> ids = this.dslContext
				.select(Tables.CODE_CASE_STAGE.ID)
				.from(Tables.CODE_CASE_STAGE)
				.where(Tables.CODE_CASE_STAGE.CASE_DEF_ID.eq(
						this.dslContext
								.select(Tables.DOC.CASE_DEF_ID)
								.from(Tables.DOC)
								.where(Tables.DOC.ID.eq(docId))))
				.orderBy(Tables.CODE_CASE_STAGE.SEQ_NR)
				.fetch();
		return ids.stream().map(id -> CodeCaseStageEnum.getCaseStage(id.value1())).toList();
	}

}
