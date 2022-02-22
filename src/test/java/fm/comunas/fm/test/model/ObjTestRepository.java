package fm.comunas.fm.test.model;

import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.fm.obj.model.FMObjRepository;
import fm.comunas.fm.test.model.db.tables.records.ObjTestVRecord;

public interface ObjTestRepository extends FMObjRepository<ObjTest, ObjTestVRecord> {

	ObjTestPartNodeRepository getNodeRepository();

	CodePartListType getNodeListType();

}
