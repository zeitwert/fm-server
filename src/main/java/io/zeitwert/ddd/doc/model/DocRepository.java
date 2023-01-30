package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;

import org.jooq.TableRecord;

public interface DocRepository<D extends Doc, V extends TableRecord<?>> extends AggregateRepository<D, V> {

	static Integer MIN_DOC_ID = 100000000; // doc_id_seq minvalue

	DocPartTransitionRepository getTransitionRepository();

	CodePartListType getTransitionListType();

	DocPartItemRepository getItemRepository();

	static boolean isDocId(Integer id) {
		return id != null && id >= MIN_DOC_ID;
	}

}
