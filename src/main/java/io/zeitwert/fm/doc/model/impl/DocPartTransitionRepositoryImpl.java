package io.zeitwert.fm.doc.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.base.DocPartTransitionRepositoryBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartTransitionRecord;

@Component("docPartTransitionRepository")
public class DocPartTransitionRepositoryImpl extends DocPartTransitionRepositoryBase {

	protected DocPartTransitionRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(appContext, dslContext);
	}

	@Override
	public DocPartTransition doCreate(Doc doc) {
		DocPartTransitionRecord dbRecord = this.getDSLContext().newRecord(Tables.DOC_PART_TRANSITION);
		return this.newPart(doc, dbRecord);
	}

	@Override
	public List<DocPartTransition> doLoad(Doc doc) {
		Result<DocPartTransitionRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.DOC_PART_TRANSITION)
				.where(Tables.DOC_PART_TRANSITION.DOC_ID.eq(doc.getId()))
				.orderBy(Tables.DOC_PART_TRANSITION.SEQ_NR)
				.fetchInto(Tables.DOC_PART_TRANSITION);
		return dbRecords.map(dbRecord -> this.newPart(doc, dbRecord));
	}

}
