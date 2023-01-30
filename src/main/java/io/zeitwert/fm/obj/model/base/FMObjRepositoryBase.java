
package io.zeitwert.fm.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.Record;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMObjRepositoryBase<O extends FMObj, V extends Record> extends ObjRepositoryBase<O, V>
		implements FMObjRepository<O, V> {

	private ObjNoteRepository noteRepository;
	private DocTaskRepository taskRepository;

	protected FMObjRepositoryBase(
			final Class<? extends AggregateRepository<O, V>> repoIntfClass,
			final Class<? extends Obj> intfClass,
			final Class<? extends Obj> baseClass,
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
