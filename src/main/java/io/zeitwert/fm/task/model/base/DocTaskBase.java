
package io.zeitwert.fm.task.model.base;

import java.time.OffsetDateTime;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;
import io.zeitwert.fm.task.model.enums.CodeTaskPriorityEnum;

public abstract class DocTaskBase extends FMDocBase implements DocTask {

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<Integer> relatedObjId;
	protected final SimpleProperty<Integer> relatedDocId;
	protected final SimpleProperty<String> subject;
	protected final SimpleProperty<String> content;
	protected final SimpleProperty<Boolean> isPrivate;
	protected final EnumProperty<CodeTaskPriority> priority;
	protected final SimpleProperty<OffsetDateTime> dueAt;
	protected final SimpleProperty<OffsetDateTime> remindAt;

	protected DocTaskBase(DocTaskRepository repository, UpdatableRecord<?> objRecord, UpdatableRecord<?> taskRecord) {
		super(repository, objRecord);
		this.dbRecord = taskRecord;
		this.relatedObjId = this.addSimpleProperty(dbRecord, DocTaskFields.RELATED_OBJ_ID);
		this.relatedDocId = this.addSimpleProperty(dbRecord, DocTaskFields.RELATED_DOC_ID);
		this.subject = this.addSimpleProperty(dbRecord, DocTaskFields.SUBJECT);
		this.content = this.addSimpleProperty(dbRecord, DocTaskFields.CONTENT);
		this.isPrivate = this.addSimpleProperty(dbRecord, DocTaskFields.IS_PRIVATE);
		this.priority = this.addEnumProperty(dbRecord, DocTaskFields.PRIORITY_ID, CodeTaskPriorityEnum.class);
		this.dueAt = this.addSimpleProperty(dbRecord, DocTaskFields.DUE_AT);
		this.remindAt = this.addSimpleProperty(dbRecord, DocTaskFields.REMIND_AT);
	}

	@Override
	public DocTaskRepository getRepository() {
		return (DocTaskRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer docId, Integer tenantId) {
		super.doInit(docId, tenantId);
		this.dbRecord.setValue(DocTaskFields.DOC_ID, docId);
		this.dbRecord.setValue(DocTaskFields.TENANT_ID, tenantId);
		CodeCaseStage initStage = CodeCaseStageEnum.getCaseStage("task.new");
		this.doInitWorkflow("task", initStage);
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public Integer getRelatedToId() {
		Integer relatedToId = this.dbRecord.getValue(DocTaskFields.RELATED_OBJ_ID);
		return relatedToId != null ? relatedToId : this.dbRecord.getValue(DocTaskFields.RELATED_DOC_ID);
	}

	@Override
	public void setRelatedToId(Integer id) {
		if (id == null) {
			this.dbRecord.setValue(DocTaskFields.RELATED_OBJ_ID, id);
			this.dbRecord.setValue(DocTaskFields.RELATED_DOC_ID, id);
		} else if (ObjRepository.isObjId(id)) {
			this.dbRecord.setValue(DocTaskFields.RELATED_OBJ_ID, id);
			this.dbRecord.setValue(DocTaskFields.RELATED_DOC_ID, null);
		} else {
			this.dbRecord.setValue(DocTaskFields.RELATED_OBJ_ID, null);
			this.dbRecord.setValue(DocTaskFields.RELATED_DOC_ID, id);
		}
	}

	@Override
	public Aggregate getRelatedTo() {
		Integer relatedToId = this.dbRecord.getValue(DocTaskFields.RELATED_OBJ_ID);
		if (relatedToId != null) {
			return this.getRepository().getObjRepository().get(relatedToId);
		}
		relatedToId = this.dbRecord.getValue(DocTaskFields.RELATED_DOC_ID);
		// if (relatedId != null) {
		// return this.getRepository().getObjRepository().get(relatedId);
		// }
		return null;
	}

	@Override
	public void setAccountId(Integer id) {
		super.account.setId(id);
		this.dbRecord.setValue(DocTaskFields.ACCOUNT_ID, id);
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.setCaption(this.getSubject());
	}

}
