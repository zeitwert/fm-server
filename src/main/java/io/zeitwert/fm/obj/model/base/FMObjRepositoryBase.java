
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
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;

public abstract class FMObjRepositoryBase<O extends FMObj, V extends Record> extends ObjRepositoryBase<O, V>
		implements FMObjRepository<O, V> {

	private static final String AREA_SET = "obj.areaSet";

	private final CodePartListType areaSetType;
	private final ObjNoteRepository noteRepository;

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
		final ObjNoteRepository noteRepository
	) {
		super(
			repoIntfClass,
			intfClass,
			baseClass,
			aggregateTypeId,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository
		);
		this.areaSetType = this.getAppContext().getPartListType(AREA_SET);
		this.noteRepository = noteRepository;
	}
	//@formatter:on

	@Override
	public CodePartListType getAreaSetType() {
		return this.areaSetType;
	}

	@Override
	public ObjNoteRepository getNoteRepository() {
		return this.noteRepository;
	}

}
