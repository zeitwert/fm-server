package fm.comunas.ddd.doc.model.base;

import java.time.OffsetDateTime;

import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.DocPartTransition;
import fm.comunas.ddd.doc.model.db.tables.records.DocPartTransitionRecord;
import fm.comunas.ddd.doc.model.enums.CodeCaseStage;
import fm.comunas.ddd.doc.model.enums.CodeCaseStageEnum;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.property.model.EnumProperty;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.SimpleProperty;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;

public abstract class DocPartTransitionBase extends DocPartBase<Doc> implements DocPartTransition {

	protected final ReferenceProperty<ObjUser> user;
	protected final SimpleProperty<OffsetDateTime> modifiedAt;
	protected final EnumProperty<CodeCaseStage> oldCaseStage;
	protected final EnumProperty<CodeCaseStage> newCaseStage;
	protected final SimpleProperty<JSON> changes;

	public DocPartTransitionBase(Doc doc, UpdatableRecord<?> dbRecord) {
		super(doc, dbRecord);
		this.user = this.addReferenceProperty(dbRecord, DocPartTransitionFields.USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(dbRecord, DocPartTransitionFields.MODIFIED_AT);
		this.oldCaseStage = this.addEnumProperty(dbRecord, DocPartTransitionFields.OLD_CASE_STAGE_ID,
				CodeCaseStageEnum.class);
		this.newCaseStage = this.addEnumProperty(dbRecord, DocPartTransitionFields.NEW_CASE_STAGE_ID,
				CodeCaseStageEnum.class);
		this.changes = this.addSimpleProperty(dbRecord, DocPartTransitionFields.CHANGES);
	}

	@Override
	public void afterCreate() {
		DocPartTransitionRecord dbRecord = (DocPartTransitionRecord) this.getDbRecord();
		dbRecord.setSeqNr(this.getAggregate().getMeta().getTransitionList().size() + 1);
		dbRecord.setUserId(this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setModifiedAt(OffsetDateTime.now());
		dbRecord.setOldCaseStageId(null);
		dbRecord.setNewCaseStageId(null);
	}

	public String getChanges() {
		JSON changes = this.changes.getValue();
		return changes != null ? changes.toString() : null;
	}

	public void setChanges(String changes) {
		DocPartTransitionRecord dbRecord = (DocPartTransitionRecord) this.getDbRecord();
		dbRecord.setChanges(JSON.valueOf(changes));
	}

}
