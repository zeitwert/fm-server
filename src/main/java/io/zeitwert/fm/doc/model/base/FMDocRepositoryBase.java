
package io.zeitwert.fm.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMDocRepositoryBase<O extends FMDoc, V extends Record> extends DocRepositoryBase<O, V>
		implements FMDocRepository<O, V> {

	private ObjNoteRepository noteRepository;
	private DocTaskRepository taskRepository;

	protected FMDocRepositoryBase(
			final Class<? extends AggregateRepository<O, V>> repoIntfClass,
			final Class<? extends Doc> intfClass,
			final Class<? extends Doc> baseClass,
			final String aggregateTypeId,
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				repoIntfClass,
				intfClass,
				baseClass,
				aggregateTypeId,
				appContext,
				dslContext);
	}

	@Override
	public ObjNoteRepository getNoteRepository() {
		if (this.noteRepository == null) {
			this.noteRepository = this.getAppContext().getBean(ObjNoteRepository.class);
		}
		return this.noteRepository;
	}

	@Override
	public DocTaskRepository getTaskRepository() {
		if (this.taskRepository == null) {
			this.taskRepository = this.getAppContext().getBean(DocTaskRepository.class);
		}
		return this.taskRepository;
	}

}
