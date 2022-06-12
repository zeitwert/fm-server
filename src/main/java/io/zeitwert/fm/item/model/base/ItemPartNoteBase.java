package io.zeitwert.fm.item.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.item.model.base.ItemPartBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartStatus;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.item.model.ItemPartNote;
import io.zeitwert.fm.item.model.db.tables.records.ItemPartNoteRecord;

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

	public ItemPartNoteBase(PartRepository<A, ?> repository, A a, UpdatableRecord<?> dbRecord) {
		super(repository, a, dbRecord);
		this.subject = this.addSimpleProperty(dbRecord, ItemPartNoteFields.SUBJECT);
		this.content = this.addSimpleProperty(dbRecord, ItemPartNoteFields.CONTENT);
		this.isPrivate = this.addSimpleProperty(dbRecord, ItemPartNoteFields.IS_PRIVATE);
		this.createdByUser = this.addReferenceProperty(dbRecord, ItemPartNoteFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(dbRecord, ItemPartNoteFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(dbRecord, ItemPartNoteFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(dbRecord, ItemPartNoteFields.MODIFIED_AT);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		ItemPartNoteRecord dbRecord = (ItemPartNoteRecord) this.getDbRecord();
		dbRecord.setCreatedByUserId(this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setCreatedAt(OffsetDateTime.now());
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getStatus() == PartStatus.UPDATED) {
			ItemPartNoteRecord dbRecord = (ItemPartNoteRecord) this.getDbRecord();
			dbRecord.setModifiedByUserId(this.getMeta().getSessionInfo().getUser().getId());
			dbRecord.setModifiedAt(OffsetDateTime.now());
		}
	}

}
