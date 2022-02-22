package fm.comunas.fm.obj.model.base;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.obj.model.base.ObjBase;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.PartListProperty;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.item.model.ItemPartNote;
import fm.comunas.fm.obj.model.FMObj;
import fm.comunas.fm.obj.model.FMObjRepository;
import fm.comunas.fm.obj.model.ObjPartNote;

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

	@Override
	public void beforeStore() {
		super.beforeStore();
		int seqNr = 0;
		for (ObjPartNote note : this.getNoteList()) {
			note.setSeqNr(seqNr++);
		}
	}

}
