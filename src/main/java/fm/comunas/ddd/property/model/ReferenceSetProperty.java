package fm.comunas.ddd.property.model;

import fm.comunas.ddd.aggregate.model.Aggregate;

import java.util.Collection;
import java.util.Set;

public interface ReferenceSetProperty<A extends Aggregate> extends CollectionProperty<A> {

	Set<Integer> getItems();

	boolean hasItem(Integer aggregateId);

	void clearItems();

	void addItem(Integer aggregateId);

	void removeItem(Integer aggregateId);

	void beforeStore();

	void loadReferenceSet(Collection<EntityPartItem> items);

}
