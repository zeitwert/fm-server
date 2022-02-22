package io.zeitwert.ddd.property.model;

public interface SimpleProperty<T> extends Property<T> {

	T getValue();

	void setValue(T value);

}
