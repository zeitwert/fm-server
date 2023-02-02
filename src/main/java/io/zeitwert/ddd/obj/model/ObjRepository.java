package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;

import org.jooq.TableRecord;

public interface ObjRepository<O extends Obj, V extends TableRecord<?>> extends AggregateRepository<O, V> {

	static boolean isObjId(Integer id) {
		return id != null && id < DocRepository.MIN_DOC_ID;
	}

	static CodePartListType transitionListType() {
		return CodePartListTypeEnum.getPartListType("obj.transitionList");
	}

	ObjPartTransitionRepository getTransitionRepository();

	ObjPartItemRepository getItemRepository();

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	void delete(O obj);

}
