
package io.zeitwert.fm.obj.model.base;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMObjRepositoryBase<O extends FMObj, V extends TableRecord<?>> extends ObjRepositoryBase<O, V>
		implements FMObjRepository<O, V> {

	private ObjNoteRepository noteRepository;
	private DocTaskRepository taskRepository;

	protected FMObjRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
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
