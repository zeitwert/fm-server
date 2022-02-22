package fm.comunas.fm.item.model.base;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.item.model.base.ItemPartBase;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.fm.item.model.ItemPartNote;
import fm.comunas.fm.item.model.db.tables.records.ItemPartNoteRecord;

import java.time.OffsetDateTime;

import org.jooq.UpdatableRecord;

public abstract class ItemPartNoteBase<A extends Aggregate> extends ItemPartBase<A> implements ItemPartNote<A> {

	protected final SimpleProperty<String> subject;
	protected final SimpleProperty<String> content;
	protected final SimpleProperty<Boolean> isPrivate;

	protected final ReferenceProperty<ObjUser> createdByUser;
	protected final SimpleProperty<OffsetDateTime> createdAt;
	protected final ReferenceProperty<ObjUser> modifiedByUser;
	protected final SimpleProperty<OffsetDateTime> modifiedAt;

	public ItemPartNoteBase(A a, UpdatableRecord<?> dbRecord) {
		super(a, dbRecord);
		this.subject = this.addSimpleProperty(dbRecord, ItemPartNoteFields.SUBJECT);
		this.content = this.addSimpleProperty(dbRecord, ItemPartNoteFields.CONTENT);
		this.isPrivate = this.addSimpleProperty(dbRecord, ItemPartNoteFields.IS_PRIVATE);
		this.createdByUser = this.addReferenceProperty(dbRecord, ItemPartNoteFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(dbRecord, ItemPartNoteFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(dbRecord, ItemPartNoteFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(dbRecord, ItemPartNoteFields.MODIFIED_AT);
	}

	@Override
	public void afterCreate() {
		ItemPartNoteRecord dbRecord = (ItemPartNoteRecord) this.getDbRecord();
		dbRecord.setCreatedByUserId(this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setCreatedAt(OffsetDateTime.now());
	}

}
