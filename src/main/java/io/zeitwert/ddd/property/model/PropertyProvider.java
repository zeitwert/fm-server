package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public interface PropertyProvider {

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
