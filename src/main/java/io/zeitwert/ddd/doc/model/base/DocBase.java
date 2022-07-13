
package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

import static io.zeitwert.ddd.util.Check.requireThis;

import io.zeitwert.ddd.aggregate.model.base.AggregateBase;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.collaboration.model.ObjNote;
import io.zeitwert.ddd.collaboration.model.ObjNoteRepository;
import io.zeitwert.ddd.collaboration.model.enums.CodeNoteType;
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
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccount;

public abstract class DocBase extends AggregateBase implements Doc, DocMeta {

	private final SessionInfo sessionInfo;
	private final DocRepository<? extends Doc, ? extends Record> repository;
	private final UpdatableRecord<?> docDbRecord;

	protected final SimpleProperty<Integer> id;
	protected final ReferenceProperty<ObjTenant> tenant;
	protected final ReferenceProperty<ObjAccount> account;
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
	protected final ReferenceProperty<ObjUser> assigneeId;

	private final PartListProperty<DocPartTransition> transitionList;

	protected DocBase(SessionInfo sessionInfo, DocRepository<? extends Doc, ? extends Record> repository,
			UpdatableRecord<?> docDbRecord) {
		this.sessionInfo = sessionInfo;
		this.repository = repository;
		this.docDbRecord = docDbRecord;
		this.id = this.addSimpleProperty(docDbRecord, DocFields.ID);
		this.tenant = this.addReferenceProperty(docDbRecord, DocFields.TENANT_ID, ObjTenant.class);
		this.account = this.addReferenceProperty(docDbRecord, DocFields.ACCOUNT_ID, ObjAccount.class);
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
		this.assigneeId = this.addReferenceProperty(docDbRecord, DocFields.ASSIGNEE_ID, ObjUser.class);
		this.transitionList = this.addPartListProperty(repository.getTransitionListType());
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
		super.doAfterCreate();
		Integer sessionUserId = this.getMeta().getSessionInfo().getUser().getId();
		this.owner.setId(sessionUserId);
		this.createdByUser.setId(sessionUserId);
		this.createdAt.setValue(this.getMeta().getSessionInfo().getCurrentTime());
		this.transitionList.addPart();
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
		this.isInWork.setValue(isInWork);
		UpdatableRecord<?> dbRecord = getDocDbRecord();
		dbRecord.setValue(DocFields.VERSION, this.getMeta().getVersion() + 1);
		dbRecord.setValue(DocFields.MODIFIED_BY_USER_ID, this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setValue(DocFields.MODIFIED_AT, this.getMeta().getSessionInfo().getCurrentTime());
	}

	@Override
	public void doStore() {
		super.doStore();
		getDocDbRecord().store();
	}

	@Override
	public void setCaseStage(CodeCaseStage caseStage) {
		requireThis(caseStage != null && !caseStage.getIsAbstract(), "valid caseStage");
		this.caseStage.setValue(caseStage);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.transitionList) {
			return (P) this.getRepository().getTransitionRepository().create(this, partListType);
		}
		return null;
	}

	// @Override
	// public DocPartItem addItem(Property<?> property, CodePartListType
	// partListType) {
	// return this.getRepository().getItemRepository().create(this, partListType);
	// }

	public List<ObjNote> getNoteList() {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		return noteRepository.getByForeignKey(this.getSessionInfo(), "related_to_id", this.getId()).stream()
				.map(onv -> noteRepository.get(this.getSessionInfo(), onv.getId())).toList();
	}

	public ObjNote addNote(CodeNoteType noteType) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.create(this.getSessionInfo());
		note.setNoteType(noteType);
		note.setRelatedToId(this.getId());
		return note;
	}

	public void removeNote(Integer noteId) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.get(this.getSessionInfo(), noteId);
		noteRepository.delete(note);
	}

}
