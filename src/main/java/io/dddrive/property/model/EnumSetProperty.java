package io.dddrive.property.model;

import java.util.Collection;
import java.util.Set;

import io.dddrive.enums.model.Enumerated;

public interface EnumSetProperty<E extends Enumerated> extends CollectionProperty<E> {

	Set<E> getItems();

	boolean hasItem(E item);

	void clearItems();

	void addItem(E item);

	void removeItem(E item);

	void loadEnums(Collection<? extends AggregatePartItem<?>> enums);

}
