package io.zeitwert.fm.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.FMObjRepository;

import java.util.List;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

public abstract class FMObjBase extends ObjBase implements FMObj {

	protected final ReferenceProperty<ObjAccount> account;

	protected FMObjBase(SessionInfo sessionInfo, ObjRepository<? extends Obj, ? extends Record> repository,
			UpdatableRecord<?> objRecord) {
		super(sessionInfo, repository, objRecord);
		this.account = this.addReferenceProperty(objRecord, ObjFields.ACCOUNT_ID, ObjAccount.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public FMObjRepository<? extends FMObj, ? extends Record> getRepository() {
		return (FMObjRepository<? extends FMObj, ? extends Record>) super.getRepository();
	}

	@Override
	public List<ObjNoteVRecord> getNoteList() {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		return noteRepository.getByForeignKey(this.getSessionInfo(), "related_to_id", this.getId()).stream()
				.filter(onv -> !onv.getIsPrivate() || onv.getCreatedByUserId().equals(this.getSessionInfo().getUser().getId()))
				.toList();
	}

	@Override
	public ObjNote addNote(CodeNoteType noteType) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.create(this.getSessionInfo());
		note.setNoteType(noteType);
		note.setRelatedToId(this.getId());
		return note;
	}

	@Override
	public void removeNote(Integer noteId) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.get(this.getSessionInfo(), noteId);
		noteRepository.delete(note);
	}

}
