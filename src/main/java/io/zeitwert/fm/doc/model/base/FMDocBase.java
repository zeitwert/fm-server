package io.zeitwert.fm.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.base.DocBase;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.doc.model.DocPartNote;
import io.zeitwert.fm.doc.model.DocPartNoteRepository;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

public abstract class FMDocBase extends DocBase implements FMDoc {

	protected final PartListProperty<DocPartNote> noteList;

	protected FMDocBase(SessionInfo sessionInfo, DocRepository<? extends Doc, ? extends Record> repository,
			UpdatableRecord<?> docRecord) {
		super(sessionInfo, repository, docRecord);
		this.noteList = this.addPartListProperty(((FMDocRepository<?, ?>) this.getRepository()).getNoteListType());
	}

	@Override
	@SuppressWarnings("unchecked")
	public FMDocRepository<? extends FMDoc, ? extends Record> getRepository() {
		return (FMDocRepository<? extends FMDoc, ? extends Record>) super.getRepository();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		DocPartNoteRepository noteRepo = this.getRepository().getNoteRepository();
		this.noteList.loadPartList(noteRepo.getPartList(this, this.getRepository().getNoteListType()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.noteList) {
			return (P) ((FMDocRepository<?, ?>) this.getRepository()).getNoteRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

}
