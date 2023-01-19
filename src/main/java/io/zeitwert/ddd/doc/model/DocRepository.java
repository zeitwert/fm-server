package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import org.jooq.Record;

public interface DocRepository<D extends Doc, V extends Record> extends AggregateRepository<D, V> {

	static Integer MIN_DOC_ID = 100000000; // doc_id_seq minvalue

	DocPartTransitionRepository getTransitionRepository();

	CodePartListType getTransitionListType();

	// DocPartItemRepository getItemRepository();

	static boolean isDocId(Integer id) {
		return id != null && id >= MIN_DOC_ID;
	}

}
