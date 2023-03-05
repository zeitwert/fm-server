
package io.zeitwert.fm.task.model;

import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.doc.service.api.DocVCache;
import io.zeitwert.fm.obj.service.api.ObjVCache;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public interface DocTaskRepository extends FMDocRepository<DocTask, DocTaskVRecord> {

	ObjVCache getObjCache();

	DocVCache getDocCache();

	ObjAccountCache getAccountCache();

}
