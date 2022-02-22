package fm.comunas.ddd.obj.model.base;

import java.time.OffsetDateTime;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPartTransition;
import fm.comunas.ddd.obj.model.db.tables.records.ObjPartTransitionRecord;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.SimpleProperty;

import org.jooq.JSON;
import org.jooq.UpdatableRecord;

public abstract class ObjPartTransitionBase extends ObjPartBase<Obj> implements ObjPartTransition {

	protected final ReferenceProperty<ObjUser> user;
	protected final SimpleProperty<OffsetDateTime> modifiedAt;
	protected final SimpleProperty<JSON> changes;

	public ObjPartTransitionBase(Obj obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
		this.user = this.addReferenceProperty(dbRecord, ObjPartTransitionFields.USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(dbRecord, ObjPartTransitionFields.MODIFIED_AT);
		this.changes = this.addSimpleProperty(dbRecord, ObjPartTransitionFields.CHANGES);
	}

	@Override
	public void afterCreate() {
		ObjPartTransitionRecord dbRecord = (ObjPartTransitionRecord) this.getDbRecord();
		dbRecord.setSeqNr(this.getAggregate().getMeta().getTransitionList().size() + 1);
		dbRecord.setUserId(this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setModifiedAt(OffsetDateTime.now());
	}

	public String getChanges() {
		JSON changes = this.changes.getValue();
		return changes != null ? changes.toString() : null;
	}

	public void setChanges(String changes) {
		ObjPartTransitionRecord dbRecord = (ObjPartTransitionRecord) this.getDbRecord();
		dbRecord.setChanges(JSON.valueOf(changes));
	}

}
