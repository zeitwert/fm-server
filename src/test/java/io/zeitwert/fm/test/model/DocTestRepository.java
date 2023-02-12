package io.zeitwert.fm.test.model;

import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.doc.model.DocRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.test.model.db.tables.records.DocTestVRecord;

public interface DocTestRepository extends DocRepository<DocTest, DocTestVRecord> {

	default ObjNoteRepository getNoteRepository() {
		return this.getAppContext().getBean(ObjNoteRepository.class);
	}

	default DocTaskRepository getTaskRepository() {
		return this.getAppContext().getBean(DocTaskRepository.class);
	}

	static CodePartListType countrySetType() {
		return CodePartListTypeEnum.getPartListType("test.countrySet");
	}

	static CodePartListType nodeListType() {
		return CodePartListTypeEnum.getPartListType("test.nodeList");
	}

}
