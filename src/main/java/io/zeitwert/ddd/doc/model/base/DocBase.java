
package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.base.AggregateBase;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.doc.api.DocService;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocMeta;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class DocBase extends AggregateBase implements Doc, DocMeta {

	private final SessionInfo sessionInfo;
	private final DocRepository<? extends Doc, ? extends Record> repository;
	private final UpdatableRecord<?> docDbRecord;

	protected final SimpleProperty<Integer> id;
	protected final ReferenceProperty<ObjTenant> tenant;
	protected final ReferenceProperty<ObjUser> owner;
	protected final SimpleProperty<String> caption;
	protected final ReferenceProperty<ObjUser> createdByUser;
	protected final SimpleProperty<OffsetDateTime> createdAt;
	protected final ReferenceProperty<ObjUser> modifiedByUser;
	protected final SimpleProperty<OffsetDateTime> modifiedAt;

	protected final SimpleProperty<String> docTypeId;
	protected final SimpleProperty<String> caseDefId;
	protected final EnumProperty<CodeCaseStage> caseStage;
	protected final SimpleProperty<Boolean> isInWork;
	protected final ReferenceProperty<ObjUser> assigneeId;

	private final List<DocPartTransition> transitionList = new ArrayList<>();
	// private final List<ItemPartNoteImpl> notes = new ArrayList<>();

	protected DocBase(SessionInfo sessionInfo, DocRepository<? extends Doc, ? extends Record> repository,
			UpdatableRecord<?> docDbRecord) {
		this.sessionInfo = sessionInfo;
		this.repository = repository;
		this.docDbRecord = docDbRecord;
		this.id = this.addSimpleProperty(docDbRecord, DocFields.ID);
		this.tenant = this.addReferenceProperty(docDbRecord, DocFields.TENANT_ID, ObjTenant.class);
		this.owner = this.addReferenceProperty(docDbRecord, DocFields.OWNER_ID, ObjUser.class);
		this.caption = this.addSimpleProperty(docDbRecord, DocFields.CAPTION);
		this.createdByUser = this.addReferenceProperty(docDbRecord, DocFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(docDbRecord, DocFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(docDbRecord, DocFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(docDbRecord, DocFields.MODIFIED_AT);
		this.docTypeId = this.addSimpleProperty(docDbRecord, DocFields.DOC_TYPE_ID);
		this.caseDefId = this.addSimpleProperty(docDbRecord, DocFields.CASE_DEF_ID);
		this.caseStage = this.addEnumProperty(docDbRecord, DocFields.CASE_STAGE_ID, CodeCaseStageEnum.class);
		this.isInWork = this.addSimpleProperty(docDbRecord, DocFields.IS_IN_WORK);
		this.assigneeId = this.addReferenceProperty(docDbRecord, DocFields.ASSIGNEE_ID, ObjUser.class);
	}

	@Override
	public DocMeta getMeta() {
		return this;
	}

	@Override
	public SessionInfo getSessionInfo() {
		return this.sessionInfo;
	}

	@Override
	public DocRepository<? extends Doc, ? extends Record> getRepository() {
		return this.repository;
	}

	protected final UpdatableRecord<?> getDocDbRecord() {
		return this.docDbRecord;
	}

	protected DocService getDocService() {
		return this.getAppContext().getBean(DocService.class);
	}

	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.docTypeId.getValue());
	}

	@Override
	public void doInit(Integer docId, Integer tenantId) {
		this.docTypeId.setValue(this.getRepository().getAggregateType().getId());
		this.id.setValue(docId);
		this.tenant.setId(tenantId);
	}

	void doInitWorkflow(String caseDefId, CodeCaseStage defaultInitCaseStage) {
		this.caseDefId.setValue(caseDefId);
		if (this.getCaseStage() != null) {
			this.caseStage.setValue(this.getCaseStage());
		} else {
			this.caseStage.setValue(defaultInitCaseStage);
		}
	}

	@Override
	public void doAfterCreate() {
		this.createdByUser.setId(this.getMeta().getSessionInfo().getUser().getId());
		this.createdAt.setValue(OffsetDateTime.now());
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		this.addTransition();
		boolean isInWork = !"terminal".equals(this.getCaseStage().getCaseStageTypeId());
		this.isInWork.setValue(isInWork);
		UpdatableRecord<?> dbRecord = getDocDbRecord();
		dbRecord.setValue(DocFields.MODIFIED_BY_USER_ID, this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setValue(DocFields.MODIFIED_AT, OffsetDateTime.now());
	}

	@Override
	public void doStore() {
		getDocDbRecord().store();
	}

	@Override
	public void setCaseStage(CodeCaseStage caseStage) {
		require(caseStage != null && !caseStage.getIsAbstract(), "valid caseStage");
		this.caseStage.setValue(caseStage);
	}

	@Override
	public List<DocPartTransition> getTransitionList() {
		return List.copyOf(this.transitionList);
	}

	private void addTransition(DocPartTransition transition) {
		this.transitionList.add(transition);
	}

	void addTransition() {
		DocPartTransitionRepository transitionRepo = this.getRepository().getTransitionRepository();
		DocPartTransition transition = transitionRepo.create(this,
				this.getAppContext().getPartListType(DocFields.TRANSITION_LIST));
		this.addTransition(transition);
	}

	void loadTransitionList(List<DocPartTransition> transitions) {
		this.transitionList.clear();
		transitions.forEach(t -> this.addTransition(t));
	}

	@SuppressWarnings("unchecked")
	public <D extends Doc> D getInstance() {
		return (D) this.getAppContext().getRepository(this.getInstanceClass()).get(this.getSessionInfo(), this.getId());
	}

	private Class<? extends Doc> getInstanceClass() {
		return null;
	}

	protected void doCalcAll() {
	}

}
