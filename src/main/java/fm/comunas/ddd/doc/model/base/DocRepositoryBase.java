
package fm.comunas.ddd.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.aggregate.model.base.AggregateRepositoryBase;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.DocPartTransitionRepository;
import fm.comunas.ddd.doc.model.DocRepository;
import fm.comunas.ddd.doc.model.db.Tables;
import fm.comunas.ddd.doc.model.db.tables.records.DocRecord;
import fm.comunas.ddd.doc.model.enums.CodeCaseStage;
import fm.comunas.ddd.doc.model.enums.CodeCaseStageEnum;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;

import java.util.Optional;

public abstract class DocRepositoryBase<D extends Doc, V extends Record> extends AggregateRepositoryBase<D, V>
		implements DocRepository<D, V> {

	private static final String DOC_ID_SEQ = "doc_id_seq";

	private final DocPartTransitionRepository transitionRepository;
	private final CodePartListType transitionListType;

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
	}
	//@formatter:on

	public DocPartTransitionRepository getTransitionRepository() {
		return this.transitionRepository;
	}

	@Override
	public CodePartListType getTransitionListType() {
		return this.transitionListType;
	}

	protected DocRepositoryUtil getUtil() {
		return DocRepositoryUtil.getInstance();
	}

	protected Optional<D> doLoad(SessionInfo sessionInfo, Integer docId, UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.dslContext.fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null || extnRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return Optional.of(newAggregate(sessionInfo, docRecord, extnRecord));
	}

	@Override
	public void doLoadParts(D doc) {
		super.doLoadParts(doc);
		this.transitionRepository.load(doc);
		((DocBase) doc).loadTransitionList(this.transitionRepository.getPartList(doc, this.getTransitionListType()));
	}

	@Override
	public Integer nextAggregateId() {
		return this.dslContext.nextval(DOC_ID_SEQ).intValue();
	}

	protected D doCreate(SessionInfo sessionInfo, UpdatableRecord<?> extnRecord) {
		return newAggregate(sessionInfo, this.dslContext.newRecord(Tables.DOC), extnRecord);
	}

	protected void doInit(D doc, Integer docId, String caseDefId, String defaultInitCaseStageId) {
		CodeCaseStage defaultCaseStage = this.getAppContext().getEnumeration(CodeCaseStageEnum.class)
				.getItem(defaultInitCaseStageId);
		((DocBase) doc).doInit(caseDefId, defaultCaseStage);
	}

	@Override
	public void doInitParts(D doc) {
		super.doInitParts(doc);
		this.transitionRepository.init(doc);
		((DocBase) doc).addTransition();
	}

	@Override
	public void doStoreParts(D doc) {
		super.doStoreParts(doc);
		this.transitionRepository.store(doc);
	}

	@Override
	public void afterStore(D doc) {
		super.afterStore(doc);
	}

}
