
package io.zeitwert.fm.task.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import java.time.OffsetDateTime;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;
import io.zeitwert.fm.task.model.enums.CodeTaskPriorityEnum;

public abstract class DocTaskBase extends FMDocBase implements DocTask {

	protected final SimpleProperty<Integer> relatedObjId;
	protected final SimpleProperty<Integer> relatedDocId;
	protected final SimpleProperty<String> subject;
	protected final SimpleProperty<String> content;
	protected final SimpleProperty<Boolean> isPrivate;
	protected final EnumProperty<CodeTaskPriority> priority;
	protected final SimpleProperty<OffsetDateTime> dueAt;
	protected final SimpleProperty<OffsetDateTime> remindAt;

	protected DocTaskBase(DocTaskRepository repository, UpdatableRecord<?> objRecord, UpdatableRecord<?> taskRecord) {
		super(repository, objRecord, taskRecord);
		this.relatedObjId = this.addSimpleProperty(this.extnDbRecord(), DocTaskFields.RELATED_OBJ_ID);
		this.relatedDocId = this.addSimpleProperty(this.extnDbRecord(), DocTaskFields.RELATED_DOC_ID);
		this.subject = this.addSimpleProperty(this.extnDbRecord(), DocTaskFields.SUBJECT);
		this.content = this.addSimpleProperty(this.extnDbRecord(), DocTaskFields.CONTENT);
		this.isPrivate = this.addSimpleProperty(this.extnDbRecord(), DocTaskFields.IS_PRIVATE);
		this.priority = this.addEnumProperty(this.extnDbRecord(), DocTaskFields.PRIORITY_ID, CodeTaskPriorityEnum.class);
		this.dueAt = this.addSimpleProperty(this.extnDbRecord(), DocTaskFields.DUE_AT);
		this.remindAt = this.addSimpleProperty(this.extnDbRecord(), DocTaskFields.REMIND_AT);
	}

	@Override
	public DocTaskRepository getRepository() {
		return (DocTaskRepository) super.getRepository();
	}

	@Override
	public void doInitWorkflow() {
		CodeCaseStage initStage = CodeCaseStageEnum.getCaseStage("task.new");
		this.doInitWorkflow("task", initStage);
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		requireThis(this.getRelatedToId() != null, "relatedTo not null");
		if (this.getAccountId() == null) { // TODO: set accountId to relatedTo's accountId
			assertThis(ObjRepository.isObjId(this.getRelatedToId()), "relatedTo is obj (doc nyi)");
			this.setAccountId(this.getMeta().getRequestContext().getAccountId());
		}
		assertThis(this.getAccountId() != null, "account not null");
	}

	@Override
	public void doCalcSearch() {
		this.addSearchText(this.getSubject());
		this.addSearchText(this.getContent());
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public Integer getRelatedToId() {
		Integer relatedToId = this.extnDbRecord().getValue(DocTaskFields.RELATED_OBJ_ID);
		return relatedToId != null ? relatedToId : this.extnDbRecord().getValue(DocTaskFields.RELATED_DOC_ID);
	}

	@Override
	public void setRelatedToId(Integer id) {
		if (id == null) {
			this.extnDbRecord().setValue(DocTaskFields.RELATED_OBJ_ID, id);
			this.extnDbRecord().setValue(DocTaskFields.RELATED_DOC_ID, id);
		} else if (ObjRepository.isObjId(id)) {
			this.extnDbRecord().setValue(DocTaskFields.RELATED_OBJ_ID, id);
			this.extnDbRecord().setValue(DocTaskFields.RELATED_DOC_ID, null);
		} else {
			this.extnDbRecord().setValue(DocTaskFields.RELATED_OBJ_ID, null);
			this.extnDbRecord().setValue(DocTaskFields.RELATED_DOC_ID, id);
		}
	}

	@Override
	public Aggregate getRelatedTo() {
		Integer relatedToId = this.extnDbRecord().getValue(DocTaskFields.RELATED_OBJ_ID);
		if (relatedToId != null) {
			return this.getRepository().getObjRepository().get(relatedToId);
		}
		relatedToId = this.extnDbRecord().getValue(DocTaskFields.RELATED_DOC_ID);
		// if (relatedId != null) {
		// return this.getRepository().getObjRepository().get(relatedId);
		// }
		return null;
	}

	@Override
	public void setAccountId(Integer id) {
		super.account.setId(id);
		this.extnDbRecord().setValue(DocTaskFields.ACCOUNT_ID, id);
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
