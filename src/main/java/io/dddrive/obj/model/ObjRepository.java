package io.dddrive.obj.model;

import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.doc.model.DocRepository;

public interface ObjRepository<O extends Obj, V extends Object> extends AggregateRepository<O, V> {

	static boolean isObjId(Integer id) {
		return id != null && id < DocRepository.MIN_DOC_ID;
	}

	static CodePartListType transitionListType() {
		return CodePartListTypeEnum.getPartListType("obj.transitionList");
	}

	default ObjPartTransitionRepository getTransitionRepository() {
		return this.getAppContext().getBean(ObjPartTransitionRepository.class);
	}

	default ObjPartItemRepository getItemRepository() {
		return this.getAppContext().getBean(ObjPartItemRepository.class);
	}

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	void delete(O obj);

}
