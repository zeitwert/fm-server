
package io.zeitwert.ddd.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.db.Tables;
import io.zeitwert.ddd.doc.model.db.tables.records.DocRecord;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class DocRepositoryBase<D extends Doc, V extends Record> extends AggregateRepositoryBase<D, V>
		implements DocRepository<D, V> {

	private static final String DOC_ID_SEQ = "doc_id_seq";

	private final DocPartTransitionRepository transitionRepository;
	private final CodePartListType transitionListType;
	private final CodePartListType areaSetType;

	//@formatter:off
	protected DocRepositoryBase(
		final Class<? extends AggregateRepository<D, V>> repoIntfClass,
		final Class<? extends Doc> intfClass,
		final Class<? extends Doc> baseClass,
		final String aggregateTypeId,
		final AppContext appContext,
		final DSLContext dslContext,
		final DocPartTransitionRepository transitionRepository
	) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext);
		this.transitionRepository = transitionRepository;
		this.transitionListType = this.getAppContext().getPartListType(DocFields.TRANSITION_LIST);
		this.areaSetType = this.getAppContext().getPartListType(DocFields.AREA_SET);
	}
	//@formatter:on

	@Override
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
	}

	public DocPartTransitionRepository getTransitionRepository() {
		return this.transitionRepository;
	}

	@Override
	public CodePartListType getTransitionListType() {
		return this.transitionListType;
	}

	@Override
	public CodePartListType getAreaSetType() {
		return this.areaSetType;
	}

	@Override
	public Integer nextAggregateId() {
		return this.getDSLContext().nextval(DOC_ID_SEQ).intValue();
	}

	protected D doCreate(SessionInfo sessionInfo, UpdatableRecord<?> extnRecord) {
		return newAggregate(sessionInfo, this.getDSLContext().newRecord(Tables.DOC), extnRecord);
	}

	// TODO get rid of
	protected void doInitWorkflow(D doc, Integer docId, String caseDefId, String defaultInitCaseStageId) {
		CodeCaseStage defaultCaseStage = this.getAppContext().getEnumeration(CodeCaseStageEnum.class)
				.getItem(defaultInitCaseStageId);
		((DocBase) doc).doInitWorkflow(caseDefId, defaultCaseStage);
	}

	@Override
	public void doAfterCreate(D doc) {
		super.doAfterCreate(doc);
		((DocBase) doc).addTransition();
	}

	protected D doLoad(SessionInfo sessionInfo, Integer docId, UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.getDSLContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null || extnRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return newAggregate(sessionInfo, docRecord, extnRecord);
	}

	@Override
	public void doLoadParts(D doc) {
		super.doLoadParts(doc);
		((DocBase) doc).loadTransitionList(this.getTransitionRepository().getPartList(doc, this.getTransitionListType()));
	}

	@Override
	public void doAfterStore(D doc) {
		super.doAfterStore(doc);
	}

}
