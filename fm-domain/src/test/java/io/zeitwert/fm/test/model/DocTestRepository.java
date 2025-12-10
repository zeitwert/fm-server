package io.zeitwert.fm.test.model;

import io.dddrive.core.doc.model.DocRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;

/**
 * Repository interface for DocTest using the NEW dddrive framework.
 */
public interface DocTestRepository extends DocRepository<DocTest> {

	ObjAccountCache getAccountCache();

}
