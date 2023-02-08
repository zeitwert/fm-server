package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;

public interface ObjRepository<O extends Obj, V extends Object> extends AggregateRepository<O, V> {

	static boolean isObjId(Integer id) {
		return id != null && id < DocRepository.MIN_DOC_ID;
	}

	static CodePartListType transitionListType() {
		return CodePartListTypeEnum.getPartListType("obj.transitionList");
	}

	static ObjPartTransitionRepository getTransitionRepository() {
		return AppContext.getInstance().getBean(ObjPartTransitionRepository.class);
	}

	static ObjPartItemRepository getItemRepository() {
		return AppContext.getInstance().getBean(ObjPartItemRepository.class);
	}

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	void delete(O obj);

}
