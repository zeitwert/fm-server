package io.zeitwert.ddd.property.model;

import java.util.List;
import java.util.Set;

public interface ItemSetProperty<Item extends AggregatePartItem<?>> extends PartListProperty<Item> {

	Set<String> getItems();

	boolean hasItem(String itemId);

	void clearItems();

	void addItem(String itemId);

	void removeItem(String itemId);

	void loadItemSet(List<? extends Item> partList);

}
