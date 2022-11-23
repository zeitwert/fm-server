package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.enums.model.Enumerated;

import java.util.Collection;
import java.util.Set;

public interface EnumSetProperty<E extends Enumerated> extends CollectionProperty<E> {

	Set<E> getItems();

	boolean hasItem(E item);

	void clearItems();

	void addItem(E item);

	void removeItem(E item);

	void loadEnumSet(Collection<? extends AggregatePartItem<?>> enums);

}
