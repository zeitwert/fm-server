package io.dddrive.property.model;

import java.util.Collection;
import java.util.Set;

import io.dddrive.ddd.model.Aggregate;

public interface ReferenceSetProperty<A extends Aggregate> extends CollectionProperty<A> {

	Set<Integer> getItems();

	boolean hasItem(Integer aggregateId);

	void clearItems();

	void addItem(Integer aggregateId);

	void removeItem(Integer aggregateId);

	void loadReferences(Collection<? extends AggregatePartItem<?>> items);

}
