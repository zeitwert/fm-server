package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import org.jooq.Record;

public interface ObjRepository<O extends Obj, V extends Record> extends AggregateRepository<O, V> {

	ObjPartTransitionRepository getTransitionRepository();

	CodePartListType getTransitionListType();

	ObjPartItemRepository getItemRepository();

	CodePartListType getAreaSetType();

}
