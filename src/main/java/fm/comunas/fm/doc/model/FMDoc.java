package fm.comunas.fm.doc.model;

import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.fm.item.model.ItemWithNotes;

public interface FMDoc extends Doc, ItemWithNotes<Doc, DocPartNote> {

}
