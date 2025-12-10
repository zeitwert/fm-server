package io.dddrive.core.property.model;

import java.util.Set;

import io.dddrive.core.ddd.model.Aggregate;

public interface ReferenceSetProperty<A extends Aggregate> extends Property<A> {

	Set<Object> getItems();

	boolean hasItem(Object aggregateId);

	void clearItems();

	void addItem(Object aggregateId);

	void removeItem(Object aggregateId);

}
