package io.dddrive.core.property.model;

import java.util.Set;

import io.dddrive.core.enums.model.Enumerated;

public interface EnumSetProperty<E extends Enumerated> extends Property<E> {

	Set<E> getItems();

	boolean hasItem(E item);

	void clearItems();

	void addItem(E item);

	void removeItem(E item);

}
