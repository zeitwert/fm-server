
package io.zeitwert.fm.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.base.DocBase;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

import java.util.List;

import org.jooq.Record;
import org.jooq.TableRecord;

public abstract class FMDocBase extends DocBase implements FMDoc {

	protected final ReferenceProperty<ObjAccount> account = this.addReferenceProperty("account", ObjAccount.class);

	protected FMDocBase(DocRepository<? extends Doc, ? extends TableRecord<?>> repository, Object state) {
		super(repository, state);
	}

	@Override
	@SuppressWarnings("unchecked")
	public FMDocRepository<? extends FMDoc, ? extends TableRecord<?>> getRepository() {
		return (FMDocRepository<? extends FMDoc, ? extends Record>) super.getRepository();
	}

	@Override
	public List<ObjNoteVRecord> getNotes() {
		ObjNoteRepository noteRepository = FMDocRepository.getNoteRepository();
		return noteRepository.getByForeignKey("related_to_id", this.getId());
	}

	@Override
	public ObjNote addNote(CodeNoteType noteType) {
		ObjNoteRepository noteRepository = FMDocRepository.getNoteRepository();
		ObjNote note = noteRepository.create(this.getTenantId());
		note.setNoteType(noteType);
		note.setRelatedToId(this.getId());
		return note;
	}

	@Override
	public void removeNote(Integer noteId) {
		ObjNoteRepository noteRepository = FMDocRepository.getNoteRepository();
		ObjNote note = noteRepository.get(noteId);
		noteRepository.delete(note);
	}

	@Override
	public List<DocTaskVRecord> getTasks() {
		DocTaskRepository taskRepository = FMDocRepository.getTaskRepository();
		return taskRepository.getByForeignKey("related_to_obj_id", this.getId());
	}

	@Override
	public DocTask addTask() {
		DocTaskRepository taskRepository = FMDocRepository.getTaskRepository();
		DocTask task = taskRepository.create(this.getTenantId());
		task.setRelatedToId(this.getId());
		return task;
	}

}
