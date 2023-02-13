package io.zeitwert.fm.doc.model.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPartTransition;
import io.dddrive.doc.model.DocPartTransitionRepository;
import io.dddrive.doc.model.base.DocPartTransitionBase;
import io.dddrive.jooq.ddd.PartState;
import io.zeitwert.fm.doc.model.base.FMDocPartRepositoryBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartTransitionRecord;

@Component("docPartTransitionRepository")
public class DocPartTransitionRepositoryImpl extends FMDocPartRepositoryBase<Doc, DocPartTransition>
		implements DocPartTransitionRepository {

	private static final String PART_TYPE = "doc_part_transition";

	protected DocPartTransitionRepositoryImpl() {
		super(Doc.class, DocPartTransition.class, DocPartTransitionBase.class, PART_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("tenantId", PartState.BASE, "tenant_id", Integer.class);
		this.mapField("user", PartState.BASE, "user_id", Integer.class);
		this.mapField("timestamp", PartState.BASE, "timestamp", OffsetDateTime.class);
		this.mapField("oldCaseStage", PartState.BASE, "old_case_stage_id", String.class);
		this.mapField("newCaseStage", PartState.BASE, "new_case_stage_id", String.class);
	}

	@Override
	public DocPartTransition doCreate(Doc doc) {
		DocPartTransitionRecord dbRecord = this.dslContext().newRecord(Tables.DOC_PART_TRANSITION);
		return this.getRepositorySPI().newPart(doc, new PartState(dbRecord));
	}

	@Override
	public List<DocPartTransition> doLoad(Doc doc) {
		Result<DocPartTransitionRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.DOC_PART_TRANSITION)
				.where(Tables.DOC_PART_TRANSITION.DOC_ID.eq(doc.getId()))
				.orderBy(Tables.DOC_PART_TRANSITION.SEQ_NR)
				.fetchInto(Tables.DOC_PART_TRANSITION);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(doc, new PartState(dbRecord)));
	}

}
