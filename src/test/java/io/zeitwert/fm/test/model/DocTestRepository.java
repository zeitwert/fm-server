package io.zeitwert.fm.test.model;

import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

public interface DocTestRepository extends FMDocRepository<DocTest, DocTestVRecord> {

	ObjAccountCache getAccountCache();

}
