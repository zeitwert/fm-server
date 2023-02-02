package io.zeitwert.fm.test.model;

import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

public interface DocTestRepository extends FMDocRepository<DocTest, DocTestVRecord> {

	static CodePartListType countrySetType() {
		return CodePartListTypeEnum.getPartListType("test.countrySet");
	}

	static CodePartListType nodeListType() {
		return CodePartListTypeEnum.getPartListType("test.nodeList");
	}

}
