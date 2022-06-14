package io.zeitwert.ddd.collaboration.model;

import io.zeitwert.ddd.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.ddd.obj.model.ObjRepository;

public interface ObjNoteRepository extends ObjRepository<ObjNote, ObjNoteVRecord> {
}
