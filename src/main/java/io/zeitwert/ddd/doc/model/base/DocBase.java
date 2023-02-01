
package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import io.zeitwert.ddd.aggregate.model.base.AggregateBase;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocMeta;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.doc.service.api.DocService;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.RequestContext;

public abstract class DocBase extends AggregateBase implements Doc, DocMeta, DocSPI {

	private final DocRepository<? extends Doc, ? extends TableRecord<?>> repository;

	private final UpdatableRecord<?> baseDbRecord;
	private final UpdatableRecord<?> extnDbRecord;

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

	private CodeCaseStage oldCaseStage;

	protected DocBase(DocRepository<? extends Doc, ? extends TableRecord<?>> repository, UpdatableRecord<?> baseDbRecord,
			UpdatableRecord<?> extnDbRecord) {
		this.repository = repository;
		this.baseDbRecord = baseDbRecord;
		this.extnDbRecord = extnDbRecord;
		this.id = this.addSimpleProperty(baseDbRecord, DocFields.ID);
		this.tenant = this.addReferenceProperty(baseDbRecord, DocFields.TENANT_ID, ObjTenant.class);
		this.owner = this.addReferenceProperty(baseDbRecord, DocFields.OWNER_ID, ObjUser.class);
		this.caption = this.addSimpleProperty(baseDbRecord, DocFields.CAPTION);
		this.version = this.addSimpleProperty(baseDbRecord, DocFields.VERSION);
		this.createdByUser = this.addReferenceProperty(baseDbRecord, DocFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(baseDbRecord, DocFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(baseDbRecord, DocFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(baseDbRecord, DocFields.MODIFIED_AT);
		this.docTypeId = this.addSimpleProperty(baseDbRecord, DocFields.DOC_TYPE_ID);
		this.caseDefId = this.addSimpleProperty(baseDbRecord, DocFields.CASE_DEF_ID);
		this.caseStage = this.addEnumProperty(baseDbRecord, DocFields.CASE_STAGE_ID, CodeCaseStageEnum.class);
		this.isInWork = this.addSimpleProperty(baseDbRecord, DocFields.IS_IN_WORK);
		this.assignee = this.addReferenceProperty(baseDbRecord, DocFields.ASSIGNEE_ID, ObjUser.class);
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
	public DocRepository<? extends Doc, ? extends TableRecord<?>> getRepository() {
		return this.repository;
	}

	protected final UpdatableRecord<?> baseDbRecord() {
		return this.baseDbRecord;
	}

	protected final UpdatableRecord<?> extnDbRecord() {
		return this.extnDbRecord;
	}

	@Override
	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.docTypeId.getValue());
	}

	@Override
	public final void doInit(Integer docId, Integer tenantId) {
		super.doInit(docId, tenantId);
		try {
			this.disableCalc();
			this.docTypeId.setValue(this.getRepository().getAggregateType().getId());
			this.id.setValue(docId);
			this.tenant.setId(tenantId);
			if (this.extnDbRecord() != null) {
				this.extnDbRecord().setValue(DocExtnFields.DOC_ID, docId);
				this.extnDbRecord().setValue(DocExtnFields.TENANT_ID, tenantId);
			}
		} finally {
			this.enableCalc();
		}
		this.doInitWorkflow();
	}

	@Override
	public abstract void doInitWorkflow();

	protected final void doInitWorkflow(String caseDefId, CodeCaseStage defaultInitCaseStage) {
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
			OffsetDateTime now = this.getMeta().getRequestContext().getCurrentTime();
			this.createdAt.setValue(now);
			DocPartTransitionBase transition = (DocPartTransitionBase) this.transitionList.addPart();
			transition.setSeqNr(0);
			transition.timestamp.setValue(now);
			transition.newCaseStage.setValue(this.getCaseStage());
			this.oldCaseStage = this.getCaseStage();
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAfterLoad() {
		super.doAfterLoad();
		this.oldCaseStage = this.getCaseStage();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		DocPartTransitionRepository transitionRepo = this.getRepository().getTransitionRepository();
		this.transitionList.loadParts(transitionRepo.getParts(this, this.getRepository().getTransitionListType()));
	}

	@Override
	public void doBeforeStore() {

		RequestContext requestCtx = this.getMeta().getRequestContext();
		OffsetDateTime now = requestCtx.getCurrentTime();
		DocPartTransitionBase transition = (DocPartTransitionBase) this.transitionList.addPart();
		transition.setSeqNr(this.transitionList.getPartCount() - 1);
		transition.timestamp.setValue(now);
		transition.oldCaseStage.setValue(this.oldCaseStage);
		transition.newCaseStage.setValue(this.getCaseStage());

		super.doBeforeStore();

		try {
			this.disableCalc();
			this.version.setValue(this.version.getValue() + 1);
			this.modifiedByUser.setValue(requestCtx.getUser());
			this.modifiedAt.setValue(now);
		} finally {
			this.enableCalc();
		}

	}

	@Override
	public final void doStore() {
		super.doStore();
		this.baseDbRecord().store();
		this.extnDbRecord().store();
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
	public List<CodeCaseStage> getCaseStages() {
		return AppContext.getInstance().getBean(DocService.class).getCaseStages(this.getCaseStage().getCaseDefId());
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property.equals(this.transitionList)) {
			return this.getRepository().getTransitionRepository().create(this, partListType);
		}
		assertThis(false, "could instantiate part for partListType " + partListType);
		return null;
	}

	protected void setCaption(String caption) {
		this.caption.setValue(caption);
	}

}
