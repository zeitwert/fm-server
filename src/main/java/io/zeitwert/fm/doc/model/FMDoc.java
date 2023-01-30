package io.zeitwert.fm.doc.model;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface FMDoc extends Doc, ItemWithNotes, ItemWithTasks {

}
