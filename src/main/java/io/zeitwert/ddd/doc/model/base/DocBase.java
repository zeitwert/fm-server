
package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.TableRecord;

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

	//@formatter:off
	protected final SimpleProperty<Integer> id = this.addSimpleProperty("id", Integer.class);
	protected final SimpleProperty<String> docTypeId = this.addSimpleProperty("docTypeId", String.class);
	protected final ReferenceProperty<ObjTenant> tenant = this.addReferenceProperty("tenant", ObjTenant.class);
	protected final ReferenceProperty<ObjUser> owner = this.addReferenceProperty("owner", ObjUser.class);
	protected final SimpleProperty<String> caption = this.addSimpleProperty("caption", String.class);
	protected final SimpleProperty<Integer> version = this.addSimpleProperty("version", Integer.class);
	protected final ReferenceProperty<ObjUser> createdByUser = this.addReferenceProperty("createdByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> createdAt = this.addSimpleProperty("createdAt", OffsetDateTime.class);
	protected final ReferenceProperty<ObjUser> modifiedByUser = this.addReferenceProperty("modifiedByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> modifiedAt = this.addSimpleProperty("modifiedAt", OffsetDateTime.class);
	protected final SimpleProperty<String> caseDefId = this.addSimpleProperty("caseDefId", String.class);
	protected final EnumProperty<CodeCaseStage> caseStage = this.addEnumProperty("caseStage", CodeCaseStage.class);
	protected final SimpleProperty<Boolean> isInWork = this.addSimpleProperty("isInWork", Boolean.class);
	protected final ReferenceProperty<ObjUser> assignee = this.addReferenceProperty("assignee", ObjUser.class);
	private final PartListProperty<DocPartTransition> transitionList = this.addPartListProperty("transitionList", DocPartTransition.class);
	//@formatter:on

	private CodeCaseStage oldCaseStage;

	protected DocBase(DocRepository<? extends Doc, ? extends TableRecord<?>> repository, Object state) {
		super(repository, state);
	}

	@Override
	public DocRepository<?, ?> getRepository() {
		return (DocRepository<?, ?>) super.getRepository();
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
	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.docTypeId.getValue());
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
		this.transitionList.loadParts(transitionRepo.getParts(this, DocRepository.transitionListType()));
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
