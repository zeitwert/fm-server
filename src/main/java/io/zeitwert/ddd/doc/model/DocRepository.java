package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;

import org.jooq.TableRecord;

public interface DocRepository<D extends Doc, V extends TableRecord<?>> extends AggregateRepository<D, V> {

	static Integer MIN_DOC_ID = 100000000; // doc_id_seq minvalue

	static boolean isDocId(Integer id) {
		return id != null && id >= MIN_DOC_ID;
	}

	static CodePartListType transitionListType() {
		return CodePartListTypeEnum.getPartListType("doc.transitionList");
	}

	static DocPartTransitionRepository getTransitionRepository() {
		return AppContext.getInstance().getBean(DocPartTransitionRepository.class);
	}

	static DocPartItemRepository getItemRepository() {
		return AppContext.getInstance().getBean(DocPartItemRepository.class);
	}

}
