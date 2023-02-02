package io.zeitwert.fm.test.model;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.test.model.db.tables.records.ObjTestVRecord;

public interface ObjTestRepository extends FMObjRepository<ObjTest, ObjTestVRecord> {

	static CodePartListType countrySetType() {
		return CodePartListTypeEnum.getPartListType("test.countrySet");
	}

	static CodePartListType nodeListType() {
		return CodePartListTypeEnum.getPartListType("test.nodeList");
	}

	static ObjTestPartNodeRepository getNodeRepository() {
		return AppContext.getInstance().getBean(ObjTestPartNodeRepository.class);
	}

}
