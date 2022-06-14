package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.collaboration.model.ObjNoteRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import org.jooq.Record;

public interface ObjRepository<O extends Obj, V extends Record> extends AggregateRepository<O, V> {

	ObjPartTransitionRepository getTransitionRepository();

	CodePartListType getTransitionListType();

	ObjNoteRepository getNoteRepository();

	ObjPartItemRepository getItemRepository();

	CodePartListType getAreaSetType();

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	void delete(O obj);

}
