package fm.comunas.ddd.doc.model;

import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.property.model.enums.CodePartListType;

import org.jooq.Record;

public interface DocRepository<D extends Doc, V extends Record> extends AggregateRepository<D, V> {

	DocPartTransitionRepository getTransitionRepository();

	CodePartListType getTransitionListType();

	// DocPartItemRepository getItemRepository();

	// CodePartListType getAreaSetType();

}
