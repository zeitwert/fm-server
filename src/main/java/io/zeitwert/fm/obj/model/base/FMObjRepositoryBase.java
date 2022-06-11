
package io.zeitwert.fm.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.obj.model.ObjPartNoteRepository;

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
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getNoteRepository());
	}

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
		((FMObjBase) obj).loadNoteList(this.getNoteRepository().getPartList(obj, this.getNoteListType()));
	}

}
