package io.dddrive.property.model;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.Part;
import io.dddrive.enums.model.Enumerated;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;

public interface PropertyProvider {

	Class<?> getEntityClass();

	//@formatter:off

	<T> SimpleProperty<T> getSimpleProperty(EntityWithPropertiesSPI entity, String name, Class<T> type);

	<E extends Enumerated> EnumProperty<E> getEnumProperty(EntityWithPropertiesSPI entity, String name, Class<E> enumClass);

	<E extends Enumerated> EnumSetProperty<E> getEnumSetProperty(EntityWithPropertiesSPI entity, String name, Class<E> enumClass);

	<T extends Aggregate> ReferenceProperty<T> getReferenceProperty(EntityWithPropertiesSPI entity, String name, Class<T> aggregateClass);

	<T extends Aggregate> ReferenceSetProperty<T> getReferenceSetProperty(EntityWithPropertiesSPI entity, String name, Class<T> aggregateClass);

	<P extends Part<?>> PartListProperty<P> getPartListProperty(EntityWithPropertiesSPI entity, String name, Class<P> partType);

	//@formatter:on

}
