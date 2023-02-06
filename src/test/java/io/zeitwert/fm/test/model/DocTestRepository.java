package io.zeitwert.fm.test.model;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

public interface DocTestRepository extends DocRepository<DocTest, DocTestVRecord> {

	static ObjNoteRepository getNoteRepository() {
		return AppContext.getInstance().getBean(ObjNoteRepository.class);
	}

	static DocTaskRepository getTaskRepository() {
		return AppContext.getInstance().getBean(DocTaskRepository.class);
	}

	static CodePartListType countrySetType() {
		return CodePartListTypeEnum.getPartListType("test.countrySet");
	}

	static CodePartListType nodeListType() {
		return CodePartListTypeEnum.getPartListType("test.nodeList");
	}

}
