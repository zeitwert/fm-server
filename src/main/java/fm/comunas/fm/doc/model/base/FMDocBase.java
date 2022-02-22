package fm.comunas.fm.doc.model.base;

import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.DocRepository;
import fm.comunas.ddd.doc.model.base.DocBase;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.PartListProperty;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.doc.model.FMDoc;
import fm.comunas.fm.doc.model.FMDocRepository;
import fm.comunas.fm.item.model.ItemPartNote;
import fm.comunas.fm.doc.model.DocPartNote;

import java.util.Collection;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

public abstract class FMDocBase extends DocBase implements FMDoc {

	protected final PartListProperty<DocPartNote> noteList;

	protected FMDocBase(SessionInfo sessionInfo, DocRepository<? extends Doc, ? extends Record> repository,
			UpdatableRecord<?> docRecord) {
		super(sessionInfo, repository, docRecord);
		this.noteList = this.addPartListProperty(((FMDocRepository<?, ?>) this.getRepository()).getNoteListType());
	}

	public abstract void loadNoteList(Collection<ItemPartNote<Doc>> nodeList);

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.noteList) {
			return (P) ((FMDocRepository<?, ?>) this.getRepository()).getNoteRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	@Override
	public void beforeStore() {
		super.beforeStore();
		int seqNr = 0;
		for (DocPartNote note : this.getNoteList()) {
			note.setSeqNr(seqNr++);
		}
	}

}
