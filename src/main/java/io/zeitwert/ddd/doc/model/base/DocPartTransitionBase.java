package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.db.tables.records.DocPartTransitionRecord;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;

public abstract class DocPartTransitionBase extends DocPartBase<Doc> implements DocPartTransition {

	protected final ReferenceProperty<ObjUser> user;
	protected final SimpleProperty<OffsetDateTime> timestamp;
	protected final EnumProperty<CodeCaseStage> oldCaseStage;
	protected final EnumProperty<CodeCaseStage> newCaseStage;
	protected final SimpleProperty<JSON> changes;

	public DocPartTransitionBase(PartRepository<Doc, ?> repository, Doc doc, UpdatableRecord<?> dbRecord) {
		super(repository, doc, dbRecord);
		this.user = this.addReferenceProperty(dbRecord, DocPartTransitionFields.USER_ID, ObjUser.class);
		this.timestamp = this.addSimpleProperty(dbRecord, DocPartTransitionFields.TIMESTAMP);
		this.oldCaseStage = this.addEnumProperty(dbRecord, DocPartTransitionFields.OLD_CASE_STAGE_ID,
				CodeCaseStageEnum.class);
		this.newCaseStage = this.addEnumProperty(dbRecord, DocPartTransitionFields.NEW_CASE_STAGE_ID,
				CodeCaseStageEnum.class);
		this.changes = this.addSimpleProperty(dbRecord, DocPartTransitionFields.CHANGES);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		DocPartTransitionRecord dbRecord = (DocPartTransitionRecord) this.getDbRecord();
		dbRecord.setTenantId(this.getMeta().getSessionInfo().getTenant().getId());
		dbRecord.setSeqNr(this.getAggregate().getMeta().getTransitionList().size() + 1); // TODO
		dbRecord.setUserId(this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setTimestamp(this.getMeta().getSessionInfo().getCurrentTime());
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
