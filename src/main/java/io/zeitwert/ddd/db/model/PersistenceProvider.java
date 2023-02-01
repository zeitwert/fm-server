package io.zeitwert.ddd.db.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public interface PersistenceProvider<A extends Aggregate> {

	boolean isReal(); // TODO: remove after everything is implemented

	Class<?> getEntityClass();

	AggregateState getAggregateState(Aggregate aggregate);

	A doCreate();

	A doLoad(Integer id);

	<T> SimpleProperty<T> getSimpleProperty(EntityWithPropertiesSPI entity, String name, Class<T> type);

	<E extends Enumerated> EnumProperty<E> getEnumProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumClass);

	<E extends Enumerated> EnumSetProperty<E> getEnumSetProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumClass);

	<T extends Aggregate> ReferenceProperty<T> getReferenceProperty(EntityWithPropertiesSPI entity, String name,
			Class<T> aggregateClass);

	<T extends Aggregate> ReferenceSetProperty<T> getReferenceSetProperty(EntityWithPropertiesSPI entity, String name,
			Class<T> aggregateClass);

	<P extends Part<?>> PartListProperty<P> getPartListProperty(EntityWithPropertiesSPI entity, String name,
			Class<P> partType);

}
