
package io.zeitwert.fm.doc.model.base;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMDocRepositoryBase<D extends FMDoc, V extends TableRecord<?>> extends DocRepositoryBase<D, V>
		implements FMDocRepository<D, V> {

	private ObjNoteRepository noteRepository;
	private DocTaskRepository taskRepository;

	protected FMDocRepositoryBase(
			Class<? extends AggregateRepository<D, V>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
			String aggregateTypeId,
			AppContext appContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext);
	}

	@Override
	public ObjNoteRepository getNoteRepository() {
		if (this.noteRepository == null) {
			this.noteRepository = AppContext.getInstance().getBean(ObjNoteRepository.class);
		}
		return this.noteRepository;
	}

	@Override
	public DocTaskRepository getTaskRepository() {
		if (this.taskRepository == null) {
			this.taskRepository = AppContext.getInstance().getBean(DocTaskRepository.class);
		}
		return this.taskRepository;
	}

}
