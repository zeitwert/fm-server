package io.zeitwert.fm.doc.model;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.fm.item.model.ItemWithNotes;

public interface FMDoc extends Doc, ItemWithNotes<Doc, DocPartNote> {

}
