
package fm.comunas.fm.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;

import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.DocPartTransitionRepository;
import fm.comunas.ddd.doc.model.base.DocRepositoryBase;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.fm.doc.model.DocPartNoteRepository;
import fm.comunas.fm.doc.model.FMDoc;
import fm.comunas.fm.doc.model.FMDocRepository;

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
		this.getNoteRepository().load(doc);
		((FMDocBase) doc).loadNoteList(this.getNoteRepository().getPartList(doc, this.getNoteListType()));
	}

	@Override
	public void doInitParts(O doc) {
		super.doInitParts(doc);
		this.getNoteRepository().init(doc);
	}

	@Override
	public void doStoreParts(O doc) {
		super.doStoreParts(doc);
		this.getNoteRepository().store(doc);
	}

}
