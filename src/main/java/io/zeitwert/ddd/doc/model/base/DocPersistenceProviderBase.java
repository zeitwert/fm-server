package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositorySPI;
import io.zeitwert.ddd.aggregate.model.base.AggregateSPI;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.persistence.jooq.base.PersistenceProviderBase;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord;

public abstract class DocPersistenceProviderBase<D extends Doc> extends PersistenceProviderBase<D> {

	public DocPersistenceProviderBase(
			Class<? extends AggregateRepository<D, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		super(repoIntfClass, baseClass, dslContext);
		this.mapField("id", BASE, "id", Integer.class);
		this.mapField("docTypeId", BASE, "doc_type_id", String.class);
		this.mapField("tenant", BASE, "tenant_id", Integer.class);
		this.mapField("owner", BASE, "owner_id", Integer.class);
		this.mapField("caption", BASE, "caption", String.class);
		this.mapField("version", BASE, "version", Integer.class);
		this.mapField("createdByUser", BASE, "created_by_user_id", Integer.class);
		this.mapField("createdAt", BASE, "created_at", OffsetDateTime.class);
		this.mapField("modifiedByUser", BASE, "modified_by_user_id", Integer.class);
		this.mapField("modifiedAt", BASE, "modified_at", OffsetDateTime.class);
		this.mapField("caseDefId", BASE, "case_def_id", String.class);
		this.mapField("caseStage", BASE, "case_stage_id", String.class);
		this.mapField("isInWork", BASE, "is_in_work", Boolean.class);
		this.mapField("assignee", BASE, "assignee_id", Integer.class);
		this.mapCollection("transitionList", "doc.transitionList", DocPartTransition.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected D doCreate(UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.getDSLContext().newRecord(Tables.DOC);
		AggregateState state = new AggregateState(docRecord, extnRecord);
		return ((AggregateRepositorySPI<D, ?>) this.getRepository()).newAggregate(state);
	}

	@Override
	public final void doInit(D aggregate, Integer docId, Integer tenantId) {
		DocBase doc = (DocBase) aggregate;
		try {
			doc.disableCalc();
			doc.docTypeId.setValue(doc.getRepository().getAggregateType().getId());
			doc.id.setValue(docId);
			doc.tenant.setId(tenantId);
			AggregateState state = (AggregateState) doc.getAggregateState();
			UpdatableRecord<?> extnRecord = state.extnRecord();
			if (extnRecord != null) {
				extnRecord.setValue(DocExtnFields.DOC_ID, docId);
				extnRecord.setValue(DocExtnFields.TENANT_ID, tenantId);
			}
			doc.doInitWorkflow();
		} finally {
			doc.enableCalc();
		}
	}

	@SuppressWarnings("unchecked")
	protected D doLoad(Integer docId, UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.getDSLContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		AggregateState state = new AggregateState(docRecord, extnRecord);
		return ((AggregateRepositorySPI<D, ?>) this.getRepository()).newAggregate(state);
	}

	@Override
	public final void doStore(D doc) {
		AggregateState state = (AggregateState) ((AggregateSPI) doc).getAggregateState();
		state.baseRecord().store();
		if (state.extnRecord() != null) {
			state.extnRecord().store();
		}
	}

}
