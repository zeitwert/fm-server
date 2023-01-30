package io.zeitwert.fm.test.model;

import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

public interface DocTestRepository extends FMDocRepository<DocTest, DocTestVRecord> {

	CodePartListType getCountrySetType();

	// ObjTestPartNodeRepository getNodeRepository();

	// CodePartListType getNodeListType();

}
