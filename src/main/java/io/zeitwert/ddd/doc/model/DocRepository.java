package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.collaboration.model.ObjNoteRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import org.jooq.Record;

public interface DocRepository<D extends Doc, V extends Record> extends AggregateRepository<D, V> {

	DocPartTransitionRepository getTransitionRepository();

	CodePartListType getTransitionListType();

	ObjNoteRepository getNoteRepository();

	// DocPartItemRepository getItemRepository();

	CodePartListType getAreaSetType();

}
