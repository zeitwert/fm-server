
package io.zeitwert.fm.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;

public abstract class FMDocRepositoryBase<O extends FMDoc, V extends Record> extends DocRepositoryBase<O, V>
		implements FMDocRepository<O, V> {

	private static final String AREA_SET = "doc.areaSet";

	private final CodePartListType areaSetType;
	private final ObjNoteRepository noteRepository;

//@formatter:off
	protected FMDocRepositoryBase(
		final Class<? extends AggregateRepository<O, V>> repoIntfClass,
		final Class<? extends Doc> intfClass,
		final Class<? extends Doc> baseClass,
		final String aggregateTypeId,
		final AppContext appContext,
		final DSLContext dslContext,
		final DocPartTransitionRepository transitionRepository,
		final ObjNoteRepository noteRepository
	) {
		super(
			repoIntfClass,
			intfClass,
			baseClass,
			aggregateTypeId,
			appContext,
			dslContext,
			transitionRepository
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
