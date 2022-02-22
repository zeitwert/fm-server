package fm.comunas.ddd.obj.model;

import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.property.model.enums.CodePartListType;

import org.jooq.Record;

public interface ObjRepository<O extends Obj, V extends Record> extends AggregateRepository<O, V> {

	ObjPartTransitionRepository getTransitionRepository();

	CodePartListType getTransitionListType();

	ObjPartItemRepository getItemRepository();

	CodePartListType getAreaSetType();

}
