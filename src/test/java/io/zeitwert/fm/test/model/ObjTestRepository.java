package io.zeitwert.fm.test.model;

import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestVRecord;

public interface ObjTestRepository extends ObjRepository<ObjTest, ObjTestVRecord> {

	static CodePartListType countrySetType() {
		return CodePartListTypeEnum.getPartListType("test.countrySet");
	}

	static CodePartListType nodeListType() {
		return CodePartListTypeEnum.getPartListType("test.nodeList");
	}

	default ObjTestPartNodeRepository getNodeRepository() {
		return this.getAppContext().getBean(ObjTestPartNodeRepository.class);
	}

}
