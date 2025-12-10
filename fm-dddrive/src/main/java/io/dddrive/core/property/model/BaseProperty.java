package io.dddrive.core.property.model;

public interface BaseProperty<T> extends Property<T> {

	T getValue();

	void setValue(T value);

	Class<T> getType();

}
