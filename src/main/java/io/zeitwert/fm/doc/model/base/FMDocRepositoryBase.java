
package io.zeitwert.fm.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.doc.model.DocPartNoteRepository;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;

public abstract class FMDocRepositoryBase<O extends FMDoc, V extends Record> extends DocRepositoryBase<O, V>
		implements FMDocRepository<O, V> {

	private final DocPartNoteRepository noteRepository;
	private final CodePartListType noteListType;

	//@formatter:off
	protected FMDocRepositoryBase(
		final Class<? extends AggregateRepository<O, V>> repoIntfClass,
		final Class<? extends Doc> intfClass,
		final Class<? extends Doc> baseClass,
		final String aggregateTypeId,
		final AppContext appContext,
		final DSLContext dslContext,
		final DocPartTransitionRepository transitionRepository,
		final DocPartNoteRepository noteRepository
	) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext, transitionRepository);
		this.noteListType = this.getAppContext().getPartListType(FMDocFields.NOTE_LIST);
		this.noteRepository = noteRepository;
	}
	//@formatter:on

	@Override
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getNoteRepository());
	}

	@Override
	public DocPartNoteRepository getNoteRepository() {
		return this.noteRepository;
	}

	@Override
	public CodePartListType getNoteListType() {
		return this.noteListType;
	}

	@Override
	public void doLoadParts(O doc) {
		super.doLoadParts(doc);
		((FMDocBase) doc).loadNoteList(this.getNoteRepository().getPartList(doc, this.getNoteListType()));
	}

}
