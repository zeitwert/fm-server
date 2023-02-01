package io.zeitwert.fm.obj.model.base;

import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

import java.util.List;

import org.jooq.Record;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;

public abstract class FMObjBase extends ObjBase implements FMObj {

	protected final ReferenceProperty<ObjAccount> account = this.addReferenceProperty("account", ObjAccount.class);

	protected FMObjBase(ObjRepository<? extends Obj, ? extends TableRecord<?>> repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> extnRecord) {
		super(repository, objRecord, extnRecord);
	}

	protected FMObjBase(ObjRepository<? extends Obj, ? extends TableRecord<?>> repository, AggregateState state) {
		super(repository, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public FMObjRepository<? extends FMObj, ? extends TableRecord<?>> getRepository() {
		return (FMObjRepository<? extends FMObj, ? extends Record>) super.getRepository();
	}

	@Override
	public List<ObjNoteVRecord> getNotes() {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		return noteRepository.getByForeignKey("related_to_id", this.getId());
	}

	@Override
	public ObjNote addNote(CodeNoteType noteType) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.create(this.getTenantId());
		note.setNoteType(noteType);
		note.setRelatedToId(this.getId());
		return note;
	}

	@Override
	public void removeNote(Integer noteId) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.get(noteId);
		noteRepository.delete(note);
	}

	@Override
	public List<DocTaskVRecord> getTasks() {
		DocTaskRepository taskRepository = this.getRepository().getTaskRepository();
		return taskRepository.getByForeignKey("related_to_obj_id", this.getId());
	}

	@Override
	public DocTask addTask() {
		DocTaskRepository taskRepository = this.getRepository().getTaskRepository();
		DocTask task = taskRepository.create(this.getTenantId());
		task.setRelatedToId(this.getId());
		return task;
	}

}
