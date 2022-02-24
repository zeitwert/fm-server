package io.zeitwert.fm.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.fm.doc.model.DocPartNote;
import io.zeitwert.fm.item.model.base.ItemPartNoteBase;

import org.jooq.UpdatableRecord;

public abstract class DocPartNoteBase extends ItemPartNoteBase<Doc> implements DocPartNote {

	public DocPartNoteBase(Doc doc, UpdatableRecord<?> dbRecord) {
		super(doc, dbRecord);
	}

}
