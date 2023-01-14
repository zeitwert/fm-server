
package io.zeitwert.fm.task.model;

import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public interface DocTaskRepository extends FMDocRepository<DocTask, DocTaskVRecord> {

	ObjVRepository getObjRepository();

	// DocVRepository getDocRepository();

}
