
package io.zeitwert.fm.collaboration.model.base;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;

public abstract class ObjNoteBase extends ObjBase implements ObjNote {

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<Integer> relatedToId;
	protected final EnumProperty<CodeNoteType> noteType;
	protected final SimpleProperty<String> subject;
	protected final SimpleProperty<String> content;
	protected final SimpleProperty<Boolean> isPrivate;

	protected ObjNoteBase(SessionInfo sessionInfo, ObjNoteRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> noteRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = noteRecord;
		this.relatedToId = this.addSimpleProperty(dbRecord, ObjNoteFields.RELATED_TO_ID);
		this.noteType = this.addEnumProperty(dbRecord, ObjNoteFields.NOTE_TYPE_ID, CodeNoteTypeEnum.class);
		this.subject = this.addSimpleProperty(dbRecord, ObjNoteFields.SUBJECT);
		this.content = this.addSimpleProperty(dbRecord, ObjNoteFields.CONTENT);
		this.isPrivate = this.addSimpleProperty(dbRecord, ObjNoteFields.IS_PRIVATE);
	}

	@Override
	public ObjNoteRepository getRepository() {
		return (ObjNoteRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjNoteFields.OBJ_ID, objId);
		this.dbRecord.setValue(ObjNoteFields.TENANT_ID, tenantId);
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
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.caption.setValue("Notiz");
	}

}
