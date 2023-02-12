
package io.dddrive.doc.model.base;

import static io.dddrive.util.Invariant.requireThis;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.base.AggregateBase;
import io.dddrive.ddd.model.enums.CodeAggregateType;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocMeta;
import io.dddrive.doc.model.DocPartItem;
import io.dddrive.doc.model.DocPartItemRepository;
import io.dddrive.doc.model.DocPartTransition;
import io.dddrive.doc.model.DocRepository;
import io.dddrive.doc.model.enums.CodeCaseDef;
import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.EnumSetProperty;
import io.dddrive.property.model.PartListProperty;
import io.dddrive.property.model.Property;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.ReferenceSetProperty;
import io.dddrive.property.model.SimpleProperty;

public abstract class DocBase extends AggregateBase implements Doc, DocMeta {

	//@formatter:off
	protected final SimpleProperty<Integer> id = this.addSimpleProperty("id", Integer.class);
	protected final SimpleProperty<String> docTypeId = this.addSimpleProperty("docTypeId", String.class);
	protected final ReferenceProperty<ObjTenant> tenant = this.addReferenceProperty("tenant", ObjTenant.class);
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	protected final ReferenceProperty<ObjUser> owner = this.addReferenceProperty("owner", ObjUser.class);
	protected final SimpleProperty<String> caption = this.addSimpleProperty("caption", String.class);
	protected final SimpleProperty<Integer> version = this.addSimpleProperty("version", Integer.class);
	protected final ReferenceProperty<ObjUser> createdByUser = this.addReferenceProperty("createdByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> createdAt = this.addSimpleProperty("createdAt", OffsetDateTime.class);
	protected final ReferenceProperty<ObjUser> modifiedByUser = this.addReferenceProperty("modifiedByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> modifiedAt = this.addSimpleProperty("modifiedAt", OffsetDateTime.class);
	protected final EnumProperty<CodeCaseDef> caseDef = this.addEnumProperty("caseDef", CodeCaseDef.class);
	protected final EnumProperty<CodeCaseStage> caseStage = this.addEnumProperty("caseStage", CodeCaseStage.class);
	protected final SimpleProperty<Boolean> isInWork = this.addSimpleProperty("isInWork", Boolean.class);
	protected final ReferenceProperty<ObjUser> assignee = this.addReferenceProperty("assignee", ObjUser.class);
	private final PartListProperty<DocPartTransition> transitionList = this.addPartListProperty("transitionList", DocPartTransition.class);
	//@formatter:on

	private CodeCaseStage oldCaseStage;

	protected DocBase(DocRepository<? extends Doc, ? extends Object> repository, Object state) {
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
	public void doInit(Integer id, Integer tenantId) {
		super.doInit(id, tenantId);
		try {
			this.disableCalc();
			this.id.setValue(id);
			this.docTypeId.setValue(this.getRepository().getAggregateType().getId());
			this.tenant.setId(tenantId);
		} finally {
			this.enableCalc();
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
		DocPartItemRepository itemRepository = this.getRepository().getItemRepository();
		for (Property<?> property : this.getProperties()) {
			if (property instanceof EnumSetProperty<?>) {
				EnumSetProperty<?> enumSet = (EnumSetProperty<?>) property;
				List<DocPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
				enumSet.loadEnums(partList);
			} else if (property instanceof ReferenceSetProperty<?>) {
				ReferenceSetProperty<?> referenceSet = (ReferenceSetProperty<?>) property;
				List<DocPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
				referenceSet.loadReferences(partList);
			}
		}
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
	public void setCaseDef(CodeCaseDef caseDef) {
		requireThis(this.getMeta().getCaseDef() == null, "caseDef empty");
		requireThis(caseDef != null, "caseDef not null");
		this.caseDef.setValue(caseDef);
	}

	@Override
	public void setCaseStage(CodeCaseStage caseStage) {
		requireThis(caseStage != null && !caseStage.getIsAbstract(), "valid caseStage (i)");
		if (caseStage == null) { // make compiler happy
			return;
		}
		requireThis(this.caseDef.getValue() == null || caseStage.getCaseDef() == this.caseDef.getValue(),
				"valid caseStage (ii)");
		requireThis(this.caseDef.getValue() == null || this.caseDef.getValue().getCaseStages().contains(caseStage),
				"valid caseStage (iii)");
		if (this.caseDef.getValue() == null) {
			this.caseDef.setValue(caseStage.getCaseDef());
		}
		this.caseStage.setValue(caseStage);
		this.isInWork.setValue(caseStage != null && caseStage.isInWork());
	}

	@Override
	public List<CodeCaseStage> getCaseStages() {
		return this.getCaseDef().getCaseStages();
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property instanceof EnumSetProperty<?>) {
			return this.getRepository().getItemRepository().create(this, partListType);
		} else if (property instanceof ReferenceSetProperty<?>) {
			return this.getRepository().getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	protected void setCaption(String caption) {
		this.caption.setValue(caption);
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getId() + "");
		if (this.getId() < DocRepository.MIN_DOC_ID) {
			this.addSearchToken((this.getId() % DocRepository.MIN_DOC_ID) + "");
		}
	}

}
