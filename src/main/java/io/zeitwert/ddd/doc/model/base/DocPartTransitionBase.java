package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.RequestContext;

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
		UpdatableRecord<?> dbRecord = this.getDbRecord();
		dbRecord.set(DocPartTransitionFields.TENANT_ID, this.getAggregate().getTenantId());
		RequestContext requestCtx = this.getMeta().getRequestContext();
		dbRecord.set(DocPartTransitionFields.USER_ID, requestCtx.getUser().getId());
		dbRecord.set(DocPartTransitionFields.TIMESTAMP, requestCtx.getCurrentTime());
		dbRecord.set(DocPartTransitionFields.OLD_CASE_STAGE_ID, null);
		dbRecord.set(DocPartTransitionFields.NEW_CASE_STAGE_ID, null);
	}

	@Override
	public String getChanges() {
		JSON changes = this.changes.getValue();
		return changes != null ? changes.toString() : null;
	}

	public void setChanges(String changes) {
		UpdatableRecord<?> dbRecord = this.getDbRecord();
		dbRecord.set(DocPartTransitionFields.CHANGES, JSON.valueOf(changes));
	}

}
