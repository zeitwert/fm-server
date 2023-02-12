
package io.zeitwert.fm.task.model;

import io.dddrive.doc.model.DocRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public interface DocTaskRepository extends DocRepository<DocTask, DocTaskVRecord> {

	default ObjVRepository getObjRepository() {
		return this.getAppContext().getBean(ObjVRepository.class);
	}

	default DocVRepository getDocRepository() {
		return this.getAppContext().getBean(DocVRepository.class);
	}

	default ObjNoteRepository getNoteRepository() {
		return this.getAppContext().getBean(ObjNoteRepository.class);
	}

}
