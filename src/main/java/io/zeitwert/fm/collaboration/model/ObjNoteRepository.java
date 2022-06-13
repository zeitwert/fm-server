package io.zeitwert.fm.collaboration.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;

public interface ObjNoteRepository extends ObjRepository<ObjNote, ObjNoteVRecord> {
}
