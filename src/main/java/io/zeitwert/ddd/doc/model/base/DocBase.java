
package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

import static io.zeitwert.ddd.util.Check.requireThis;

import io.zeitwert.ddd.aggregate.model.base.AggregateBase;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocMeta;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.RequestContext;

public abstract class DocBase extends AggregateBase implements Doc, DocMeta {

	private final DocRepository<? extends Doc, ? extends Record> repository;
	private final UpdatableRecord<?> docDbRecord;

	protected final SimpleProperty<Integer> id;
	protected final ReferenceProperty<ObjTenant> tenant;
	protected final ReferenceProperty<ObjUser> owner;
	protected final SimpleProperty<String> caption;
	protected final SimpleProperty<Integer> version;
	protected final ReferenceProperty<ObjUser> createdByUser;
	protected final SimpleProperty<OffsetDateTime> createdAt;
	protected final ReferenceProperty<ObjUser> modifiedByUser;
	protected final SimpleProperty<OffsetDateTime> modifiedAt;

	protected final SimpleProperty<String> docTypeId;
	protected final SimpleProperty<String> caseDefId;
	protected final EnumProperty<CodeCaseStage> caseStage;
	protected final SimpleProperty<Boolean> isInWork;
	protected final ReferenceProperty<ObjUser> assignee;

	private final PartListProperty<DocPartTransition> transitionList;

	protected DocBase(DocRepository<? extends Doc, ? extends Record> repository, UpdatableRecord<?> docDbRecord) {
		this.repository = repository;
		this.docDbRecord = docDbRecord;
		this.id = this.addSimpleProperty(docDbRecord, DocFields.ID);
		this.tenant = this.addReferenceProperty(docDbRecord, DocFields.TENANT_ID, ObjTenant.class);
		this.owner = this.addReferenceProperty(docDbRecord, DocFields.OWNER_ID, ObjUser.class);
		this.caption = this.addSimpleProperty(docDbRecord, DocFields.CAPTION);
		this.version = this.addSimpleProperty(docDbRecord, DocFields.VERSION);
		this.createdByUser = this.addReferenceProperty(docDbRecord, DocFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(docDbRecord, DocFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(docDbRecord, DocFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(docDbRecord, DocFields.MODIFIED_AT);
		this.docTypeId = this.addSimpleProperty(docDbRecord, DocFields.DOC_TYPE_ID);
		this.caseDefId = this.addSimpleProperty(docDbRecord, DocFields.CASE_DEF_ID);
		this.caseStage = this.addEnumProperty(docDbRecord, DocFields.CASE_STAGE_ID, CodeCaseStageEnum.class);
		this.isInWork = this.addSimpleProperty(docDbRecord, DocFields.IS_IN_WORK);
		this.assignee = this.addReferenceProperty(docDbRecord, DocFields.ASSIGNEE_ID, ObjUser.class);
		this.transitionList = this.addPartListProperty(repository.getTransitionListType());
	}

	@Override
	public DocMeta getMeta() {
		return this;
	}

	@Override
	public RequestContext getRequestContext() {
		return this.getAppContext().getRequestContext();
	}

	@Override
	public Integer getTenantId() {
		return this.tenant.getId();
	}

	@Override
	public DocRepository<? extends Doc, ? extends Record> getRepository() {
		return this.repository;
	}

	protected final UpdatableRecord<?> getDocDbRecord() {
		return this.docDbRecord;
	}

	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.docTypeId.getValue());
	}

	@Override
	public void doInit(Integer docId, Integer tenantId) {
		super.doInit(docId, tenantId);
		try {
			this.disableCalc();
			this.docTypeId.setValue(this.getRepository().getAggregateType().getId());
			this.id.setValue(docId);
			this.tenant.setId(tenantId);
		} finally {
			this.enableCalc();
		}
	}

	protected void doInitWorkflow(String caseDefId, CodeCaseStage defaultInitCaseStage) {
		this.caseDefId.setValue(caseDefId);
		if (this.getCaseStage() == null) {
			this.setCaseStage(defaultInitCaseStage);
		}
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		Integer sessionUserId = this.getMeta().getRequestContext().getUser().getId();
		try {
			this.disableCalc();
			this.owner.setId(sessionUserId);
			this.version.setValue(0);
			this.createdByUser.setId(sessionUserId);
			this.createdAt.setValue(this.getMeta().getRequestContext().getCurrentTime());
			this.transitionList.addPart();
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		DocPartTransitionRepository transitionRepo = this.getRepository().getTransitionRepository();
		this.transitionList.loadPartList(transitionRepo.getPartList(this, this.getRepository().getTransitionListType()));
	}

	@Override
	public void doBeforeStore() {
		this.transitionList.addPart();
		super.doBeforeStore();
		boolean isInWork = !"terminal".equals(this.getCaseStage().getCaseStageTypeId());
		try {
			this.disableCalc();
			this.isInWork.setValue(isInWork);
			this.version.setValue(this.version.getValue() + 1);
			this.modifiedByUser.setValue(this.getMeta().getRequestContext().getUser());
			this.modifiedAt.setValue(this.getMeta().getRequestContext().getCurrentTime());
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doStore() {
		super.doStore();
		getDocDbRecord().store();
	}

	@Override
	public boolean isInWork() {
		return this.isInWork.getValue();
	}

	@Override
	public void setCaseStage(CodeCaseStage caseStage) {
		requireThis(caseStage != null && !caseStage.getIsAbstract(), "valid caseStage");
		this.caseStage.setValue(caseStage);
		this.isInWork.setValue(caseStage != null && caseStage.isInWork());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.transitionList) {
			return (P) this.getRepository().getTransitionRepository().create(this, partListType);
		}
		return null;
	}

	protected void setCaption(String caption) {
		this.caption.setValue(caption);
	}

}
