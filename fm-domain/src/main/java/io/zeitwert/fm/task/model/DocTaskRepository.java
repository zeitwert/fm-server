package io.zeitwert.fm.task.model;

import io.dddrive.core.doc.model.DocRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;

public interface DocTaskRepository extends DocRepository<DocTask> {

	ObjAccountCache getAccountCache();

}
