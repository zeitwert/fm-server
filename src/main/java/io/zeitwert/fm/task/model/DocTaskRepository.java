
package io.zeitwert.fm.task.model;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public interface DocTaskRepository extends FMDocRepository<DocTask, DocTaskVRecord> {

	static ObjVRepository getObjRepository() {
		return AppContext.getInstance().getBean(ObjVRepository.class);
	}

	static DocVRepository getDocRepository() {
		return AppContext.getInstance().getBean(DocVRepository.class);
	}

}
