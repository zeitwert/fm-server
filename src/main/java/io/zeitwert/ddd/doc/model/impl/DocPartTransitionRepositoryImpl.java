package io.zeitwert.ddd.doc.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.base.DocPartRepositoryBase;
import io.zeitwert.ddd.doc.model.base.DocPartTransitionBase;
import io.zeitwert.ddd.doc.model.db.Tables;
import io.zeitwert.ddd.doc.model.db.tables.records.DocPartTransitionRecord;

import java.util.List;

@Component("docPartTransitionRepository")
public class DocPartTransitionRepositoryImpl extends DocPartRepositoryBase<Doc, DocPartTransition>
		implements DocPartTransitionRepository {

	private static final String PART_TYPE = "doc_part_transition";

	@Autowired
	//@formatter:off
	protected DocPartTransitionRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			Doc.class,
			DocPartTransition.class,
			DocPartTransitionBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public DocPartTransition doCreate(Doc doc) {
		DocPartTransitionRecord dbRecord = this.getDSLContext().newRecord(Tables.DOC_PART_TRANSITION);
		return this.newPart(doc, dbRecord);
	}

	@Override
	public List<DocPartTransition> doLoad(Doc doc) {
		//@formatter:off
		Result<DocPartTransitionRecord> dbRecords = this.getDSLContext()
			.selectFrom(Tables.DOC_PART_TRANSITION)
			.where(Tables.DOC_PART_TRANSITION.DOC_ID.eq(doc.getId()))
			.orderBy(Tables.DOC_PART_TRANSITION.SEQ_NR)
			.fetchInto(Tables.DOC_PART_TRANSITION);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(doc, dbRecord));
	}

}
