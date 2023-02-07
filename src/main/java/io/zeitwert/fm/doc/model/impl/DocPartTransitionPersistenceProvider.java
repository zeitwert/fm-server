package io.zeitwert.fm.doc.model.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartTransitionRecord;
import io.zeitwert.jooq.persistence.DocPartPersistenceProviderBase;
import io.zeitwert.jooq.persistence.PartState;

@Configuration
public class DocPartTransitionPersistenceProvider extends DocPartPersistenceProviderBase<Doc, DocPartTransition> {

	public DocPartTransitionPersistenceProvider(DSLContext dslContext) {
		super(DocPartTransition.class, dslContext);
		this.mapField("tenantId", PartState.BASE, "tenant_id", Integer.class);
		this.mapField("user", PartState.BASE, "user_id", Integer.class);
		this.mapField("timestamp", PartState.BASE, "timestamp", OffsetDateTime.class);
		this.mapField("oldCaseStage", PartState.BASE, "old_case_stage_id", String.class);
		this.mapField("newCaseStage", PartState.BASE, "new_case_stage_id", String.class);
		this.mapField("changes", PartState.BASE, "changes", JSON.class);
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
