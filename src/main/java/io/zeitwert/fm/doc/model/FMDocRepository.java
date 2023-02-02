package io.zeitwert.fm.doc.model;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

import org.jooq.TableRecord;

public interface FMDocRepository<D extends Doc, V extends TableRecord<?>> extends DocRepository<D, V> {

	static ObjNoteRepository getNoteRepository() {
		return AppContext.getInstance().getBean(ObjNoteRepository.class);
	}

	static DocTaskRepository getTaskRepository() {
		return AppContext.getInstance().getBean(DocTaskRepository.class);
	}

}
