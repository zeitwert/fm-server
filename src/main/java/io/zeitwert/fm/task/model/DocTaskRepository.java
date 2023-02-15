
package io.zeitwert.fm.task.model;

import io.dddrive.doc.model.DocRepository;
import io.zeitwert.fm.collaboration.service.api.ObjNoteCache;
import io.zeitwert.fm.doc.service.api.DocVCache;
import io.zeitwert.fm.obj.service.api.ObjVCache;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public interface DocTaskRepository extends DocRepository<DocTask, DocTaskVRecord> {

	default ObjVCache getObjCache() {
		return this.getAppContext().getBean(ObjVCache.class);
	}

	default DocVCache getDocCache() {
		return this.getAppContext().getBean(DocVCache.class);
	}

	default ObjNoteCache getNoteCache() {
		return this.getAppContext().getBean(ObjNoteCache.class);
	}

}
