package io.zeitwert.fm.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.dddrive.ddd.model.AggregatePersistenceProvider;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.base.AggregateRepositorySPI;
import io.dddrive.ddd.model.base.AggregateSPI;
import io.dddrive.doc.model.Doc;
import io.dddrive.jooq.ddd.AggregateState;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord;

public interface DocPersistenceProviderMixin<D extends Doc>
		extends AggregatePersistenceProvider<D> {

	static final String DOC_ID_SEQ = "doc_id_seq";

	DSLContext dslContext();

	AggregateRepository<D, ?> getRepository();

	@Override
	default Integer nextAggregateId() {
		return this.dslContext().nextval(DOC_ID_SEQ).intValue();
	}

	@SuppressWarnings("unchecked")
	default D doCreate(UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.dslContext().newRecord(Tables.DOC);
		AggregateState state = new AggregateState(docRecord, extnRecord);
		return ((AggregateRepositorySPI<D, ?>) this.getRepository()).newAggregate(state);
	}

	@SuppressWarnings("unchecked")
	default D doLoad(Integer docId, UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.dslContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		AggregateState state = new AggregateState(docRecord, extnRecord);
		return ((AggregateRepositorySPI<D, ?>) this.getRepository()).newAggregate(state);
	}

	@Override
	default void doStore(D doc) {
		AggregateState state = (AggregateState) ((AggregateSPI) doc).getAggregateState();
		state.baseRecord().store();
		if (state.extnRecord() != null) {
			state.extnRecord().store();
		}
	}

}
