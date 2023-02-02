package io.zeitwert.fm.doc.model.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.base.DocPartPersistenceProviderBase;
import io.zeitwert.ddd.doc.model.base.DocPartTransitionBase;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocPartTransitionRecord;

@Configuration
public class DocPartTransitionPersistenceProvider extends DocPartPersistenceProviderBase<Doc, DocPartTransition> {

	public DocPartTransitionPersistenceProvider(DSLContext dslContext) {
		super(Doc.class, DocPartTransitionRepository.class, DocPartTransitionBase.class, dslContext);
		this.mapField("tenantId", BASE, "tenant_id", Integer.class);
		this.mapField("user", BASE, "user_id", Integer.class);
		this.mapField("timestamp", BASE, "timestamp", OffsetDateTime.class);
		this.mapField("oldCaseStage", BASE, "old_case_stage_id", String.class);
		this.mapField("newCaseStage", BASE, "new_case_stage_id", String.class);
		this.mapField("changes", BASE, "changes", JSON.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return DocPartTransition.class;
	}

	@Override
	public DocPartTransition doCreate(Doc doc) {
		DocPartTransitionRecord dbRecord = this.getDSLContext().newRecord(Tables.DOC_PART_TRANSITION);
		return this.newPart(doc, new PartState(dbRecord));
	}

	@Override
	public List<DocPartTransition> doLoad(Doc doc) {
		Result<DocPartTransitionRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.DOC_PART_TRANSITION)
				.where(Tables.DOC_PART_TRANSITION.DOC_ID.eq(doc.getId()))
				.orderBy(Tables.DOC_PART_TRANSITION.SEQ_NR)
				.fetchInto(Tables.DOC_PART_TRANSITION);
		return dbRecords.map(dbRecord -> this.newPart(doc, new PartState(dbRecord)));
	}

}
