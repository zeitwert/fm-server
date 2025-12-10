package io.zeitwert.fm.test.model;

import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestVRecord;

public interface ObjTestRepository extends FMObjRepository<ObjTest, ObjTestVRecord> {

	ObjTestPartNodeRepository getNodeRepository();

}
