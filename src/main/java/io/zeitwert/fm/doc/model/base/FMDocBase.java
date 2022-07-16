
package io.zeitwert.fm.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.base.DocBase;
import io.zeitwert.ddd.doc.model.base.DocFields;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;

import java.util.List;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

public abstract class FMDocBase extends DocBase implements FMDoc {

	protected final ReferenceProperty<ObjAccount> account;

	protected FMDocBase(SessionInfo sessionInfo, DocRepository<? extends Doc, ? extends Record> repository,
			UpdatableRecord<?> docRecord) {
		super(sessionInfo, repository, docRecord);
		this.account = this.addReferenceProperty(docRecord, DocFields.ACCOUNT_ID, ObjAccount.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public FMDocRepository<? extends FMDoc, ? extends Record> getRepository() {
		return (FMDocRepository<? extends FMDoc, ? extends Record>) super.getRepository();
	}

	public List<ObjNoteVRecord> getNoteList() {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		return noteRepository.getByForeignKey(this.getSessionInfo(), "related_to_id", this.getId()).stream()
				.filter(onv -> !onv.getIsPrivate() || onv.getCreatedByUserId().equals(this.getSessionInfo().getUser().getId()))
				.toList();
	}

	public ObjNote addNote(CodeNoteType noteType) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.create(this.getSessionInfo());
		note.setNoteType(noteType);
		note.setRelatedToId(this.getId());
		return note;
	}

	public void removeNote(Integer noteId) {
		ObjNoteRepository noteRepository = this.getRepository().getNoteRepository();
		ObjNote note = noteRepository.get(this.getSessionInfo(), noteId);
		noteRepository.delete(note);
	}

}
