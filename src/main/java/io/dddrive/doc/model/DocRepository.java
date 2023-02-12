package io.dddrive.doc.model;

import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;

public interface DocRepository<D extends Doc, V extends Object> extends AggregateRepository<D, V> {

	static Integer MIN_DOC_ID = 100000000; // doc_id_seq minvalue

	static boolean isDocId(Integer id) {
		return id != null && id >= MIN_DOC_ID;
	}

	static CodePartListType transitionListType() {
		return CodePartListTypeEnum.getPartListType("doc.transitionList");
	}

	default DocPartTransitionRepository getTransitionRepository() {
		return this.getAppContext().getBean(DocPartTransitionRepository.class);
	}

	default DocPartItemRepository getItemRepository() {
		return this.getAppContext().getBean(DocPartItemRepository.class);
	}

}
