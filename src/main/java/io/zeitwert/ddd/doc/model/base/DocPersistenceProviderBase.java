package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.db.model.jooq.AggregateStateImpl;
import io.zeitwert.ddd.db.model.jooq.PersistenceProviderBase;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
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

	protected D doCreate(UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.getDSLContext().newRecord(Tables.DOC);
		AggregateState state = new AggregateStateImpl(docRecord, extnRecord);
		return this.newAggregate(state);
	}

	protected D doLoad(Integer docId, UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.getDSLContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		AggregateState state = new AggregateStateImpl(docRecord, extnRecord);
		return this.newAggregate(state);
	}

}
