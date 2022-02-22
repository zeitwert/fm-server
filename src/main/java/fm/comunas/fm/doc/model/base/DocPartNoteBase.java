package fm.comunas.fm.doc.model.base;

import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.fm.doc.model.DocPartNote;
import fm.comunas.fm.item.model.base.ItemPartNoteBase;

import org.jooq.UpdatableRecord;

public abstract class DocPartNoteBase extends ItemPartNoteBase<Doc> implements DocPartNote {

	public DocPartNoteBase(Doc doc, UpdatableRecord<?> dbRecord) {
		super(doc, dbRecord);
	}

}
