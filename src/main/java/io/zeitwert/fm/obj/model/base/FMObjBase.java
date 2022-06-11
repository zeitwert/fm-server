package io.zeitwert.fm.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.item.model.ItemPartNote;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.obj.model.ObjPartNote;

import java.util.Collection;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

public abstract class FMObjBase extends ObjBase implements FMObj {

	protected final PartListProperty<ObjPartNote> noteList;

	protected FMObjBase(SessionInfo sessionInfo, ObjRepository<? extends Obj, ? extends Record> repository,
			UpdatableRecord<?> objRecord) {
		super(sessionInfo, repository, objRecord);
		this.noteList = this.addPartListProperty(((FMObjRepository<?, ?>) this.getRepository()).getNoteListType());
	}

	public abstract void loadNoteList(Collection<ItemPartNote<Obj>> nodeList);

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.noteList) {
			return (P) ((FMObjRepository<?, ?>) this.getRepository()).getNoteRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

}
