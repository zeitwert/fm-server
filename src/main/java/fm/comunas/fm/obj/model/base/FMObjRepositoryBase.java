
package fm.comunas.fm.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;

import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPartItemRepository;
import fm.comunas.ddd.obj.model.ObjPartTransitionRepository;
import fm.comunas.ddd.obj.model.base.ObjRepositoryBase;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.fm.obj.model.FMObj;
import fm.comunas.fm.obj.model.FMObjRepository;
import fm.comunas.fm.obj.model.ObjPartNoteRepository;

public abstract class FMObjRepositoryBase<O extends FMObj, V extends Record> extends ObjRepositoryBase<O, V>
		implements FMObjRepository<O, V> {

	private final ObjPartNoteRepository noteRepository;
	private final CodePartListType noteListType;

	//@formatter:off
	protected FMObjRepositoryBase(
		final Class<? extends AggregateRepository<O, V>> repoIntfClass,
		final Class<? extends Obj> intfClass,
		final Class<? extends Obj> baseClass,
		final String aggregateTypeId,
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjPartNoteRepository noteRepository
	) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext, transitionRepository, itemRepository);
		this.noteRepository = noteRepository;
		this.noteListType = this.getAppContext().getPartListType(FMObjFields.NOTE_LIST);
	}
	//@formatter:on

	@Override
	public ObjPartNoteRepository getNoteRepository() {
		return this.noteRepository;
	}

	@Override
	public CodePartListType getNoteListType() {
		return this.noteListType;
	}

	@Override
	public void doLoadParts(O obj) {
		super.doLoadParts(obj);
		this.getNoteRepository().load(obj);
		((FMObjBase) obj).loadNoteList(this.getNoteRepository().getPartList(obj, this.getNoteListType()));
	}

	@Override
	public void doInitParts(O obj) {
		super.doInitParts(obj);
		this.getNoteRepository().init(obj);
	}

	@Override
	public void doStoreParts(O obj) {
		super.doStoreParts(obj);
		this.getNoteRepository().store(obj);
	}

}
