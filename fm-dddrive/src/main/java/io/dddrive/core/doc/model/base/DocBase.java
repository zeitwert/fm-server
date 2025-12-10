package io.dddrive.core.doc.model.base;

import static io.dddrive.util.Invariant.requireThis;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.base.AggregateBase;
import io.dddrive.core.doc.model.Doc;
import io.dddrive.core.doc.model.DocMeta;
import io.dddrive.core.doc.model.DocPartTransition;
import io.dddrive.core.doc.model.DocRepository;
import io.dddrive.core.doc.model.enums.CodeCaseDef;
import io.dddrive.core.doc.model.enums.CodeCaseStage;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EnumProperty;
import io.dddrive.core.property.model.PartListProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.ReferenceProperty;

public abstract class DocBase extends AggregateBase implements Doc, DocMeta {

	//@formatter:off
	protected final BaseProperty<String> docTypeId = this.addBaseProperty("docTypeId", String.class);
	protected final EnumProperty<CodeCaseDef> caseDef = this.addEnumProperty("caseDef", CodeCaseDef.class);
	protected final EnumProperty<CodeCaseStage> caseStage = this.addEnumProperty("caseStage", CodeCaseStage.class);
	protected final BaseProperty<Boolean> isInWork = this.addBaseProperty("isInWork", Boolean.class);
	protected final ReferenceProperty<ObjUser> assignee = this.addReferenceProperty("assignee", ObjUser.class);
	private final PartListProperty<DocPartTransition> transitionList = this.addPartListProperty("transitionList", DocPartTransition.class);
	//@formatter:on

	private CodeCaseStage oldCaseStage;

	protected DocBase(DocRepository<? extends Doc> repository) {
		super(repository);
	}

	@Override
	public DocRepository<?> getRepository() {
		return (DocRepository<?>) super.getRepository();
	}

	@Override
	public DocMeta getMeta() {
		return this;
	}

	@Override
	public Object getTenantId() {
		return this.tenant.getId();
	}

	@Override
	public void doCreate(Object id, Object tenantId, Object userId, OffsetDateTime timestamp) {
		try {
			this.disableCalc();
			super.doCreate(id, tenantId, userId, timestamp);
			this.docTypeId.setValue(this.getRepository().getAggregateType().getId());
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAfterCreate(Object userId, OffsetDateTime timestamp) {
		super.doAfterCreate(userId, timestamp);
		try {
			this.disableCalc();
			this.owner.setId(userId);
			this.version.setValue(0);
			this.createdByUser.setId(userId);
			this.createdAt.setValue(timestamp);
		} finally {
			this.enableCalc();
		}
		// freeze until caseDef is set
		this.freeze(); // TODO reconsider
	}

	@Override
	public void doAfterLoad() {
		super.doAfterLoad();
		this.oldCaseStage = this.getCaseStage();
	}

//	@Override
//	public void doAssignParts() {
//		super.doAssignParts();
//		DocPartItemRepository itemRepository = this.getRepository().getItemRepository();
//		for (Property<?> property : this.getProperties()) {
//			if (property instanceof EnumSetProperty<?> enumSet) {
//				List<DocPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
//				enumSet.loadEnums(partList);
//			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
//				List<DocPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
//				referenceSet.loadReferences(partList);
//			}
//		}
//	}

	@Override
	public void doBeforeStore(Object userId, OffsetDateTime timestamp) {

		DocPartTransitionBase transition = (DocPartTransitionBase) this.transitionList.addPart(null);
		//transition.setSeqNr(this.transitionList.getPartCount() - 1);
		transition.user.setId(userId);
		transition.timestamp.setValue(timestamp);
		transition.oldCaseStage.setValue(this.oldCaseStage);
		transition.newCaseStage.setValue(this.getCaseStage());

		super.doBeforeStore(userId, timestamp);

		try {
			this.disableCalc();
			this.version.setValue(this.version.getValue() + 1);
			this.modifiedByUser.setId(userId);
			this.modifiedAt.setValue(timestamp);
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
		this.unfreeze();
		this.caseDef.setValue(caseDef);
	}

	@Override
	public void setCaseStage(CodeCaseStage caseStage, Object userId, OffsetDateTime timestamp) {
		requireThis(caseStage != null && !caseStage.getIsAbstract(), "valid caseStage (i)");
		requireThis(this.getCaseDef() == null || caseStage.getCaseDef() == this.getCaseDef(), "valid caseStage (ii)");
		if (this.getCaseDef() == null) {
			this.setCaseDef(caseStage.getCaseDef());
		}
		if (this.getCaseStage() == null) { // initial transition
			DocPartTransitionBase transition = (DocPartTransitionBase) this.transitionList.addPart(null);
			//transition.setSeqNr(0);
			transition.user.setId(userId);
			transition.timestamp.setValue(timestamp);
			transition.newCaseStage.setValue(caseStage);
			this.oldCaseStage = caseStage;
		}
		this.caseStage.setValue(caseStage);
		this.isInWork.setValue(caseStage.isInWork());
	}

	@Override
	public List<CodeCaseStage> getCaseStages() {
		return this.getCaseDef().getCaseStages();
	}

	@Override
	public Part<?> doAddPart(Property<?> property, Integer partId) {
		if (property == this.transitionList) {
			return this.getDirectory().getPartRepository(DocPartTransition.class).create(this, property, partId);
		}
		return super.doAddPart(property, partId);
	}

	protected void setCaption(String caption) {
		this.caption.setValue(caption);
	}

//	@Override
//	public void doCalcSearch() {
//		super.doCalcSearch();
//		//Integer orderNr = ((AggregateRepositorySPI<?>) this.getRepository()).getIdProvider().getOrderNr(this.getId());
//		//this.addSearchToken(orderNr + "");
//	}

}
