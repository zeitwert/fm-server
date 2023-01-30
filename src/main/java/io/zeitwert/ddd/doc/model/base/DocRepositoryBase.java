
package io.zeitwert.ddd.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.db.Tables;
import io.zeitwert.ddd.doc.model.db.tables.records.DocRecord;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

public abstract class DocRepositoryBase<D extends Doc, V extends Record> extends AggregateRepositoryBase<D, V>
		implements DocRepository<D, V> {

	private static final String DOC_ID_SEQ = "doc_id_seq";

	private DocPartTransitionRepository transitionRepository;
	private CodePartListType transitionListType;
	private DocPartItemRepository itemRepository;

	protected DocRepositoryBase(
			final Class<? extends AggregateRepository<D, V>> repoIntfClass,
			final Class<? extends Doc> intfClass,
			final Class<? extends Doc> baseClass,
			final String aggregateTypeId,
			final AppContext appContext,
			final DSLContext dslContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext);
	}

	@Override
	public DocPartTransitionRepository getTransitionRepository() {
		if (this.transitionRepository == null) {
			this.transitionRepository = this.getAppContext().getBean(DocPartTransitionRepository.class);
		}
		return this.transitionRepository;
	}

	@Override
	public CodePartListType getTransitionListType() {
		if (this.transitionListType == null) {
			this.transitionListType = this.getAppContext().getPartListType(DocFields.TRANSITION_LIST);
		}
		return this.transitionListType;
	}

	@Override
	public DocPartItemRepository getItemRepository() {
		if (this.itemRepository == null) {
			this.itemRepository = this.getAppContext().getBean(DocPartItemRepository.class);
		}
		return this.itemRepository;
	}

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
	}

	@Override
	public Integer nextAggregateId() {
		return this.getDSLContext().nextval(DOC_ID_SEQ).intValue();
	}

	protected D doCreate(UpdatableRecord<?> extnRecord) {
		return this.newAggregate(this.getDSLContext().newRecord(Tables.DOC), extnRecord);
	}

	protected D doLoad(Integer docId, UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.getDSLContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null || extnRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.newAggregate(docRecord, extnRecord);
	}

	@Override
	public void doAfterStore(D doc) {
		super.doAfterStore(doc);
	}

}
