package fm.comunas.ddd.property.model;

import fm.comunas.ddd.enums.model.Enumerated;

import java.util.Collection;
import java.util.Set;

public interface EnumSetProperty<E extends Enumerated> extends CollectionProperty<E> {

	Set<E> getItems();

	boolean hasItem(E item);

	void clearItems();

	void addItem(E item);

	void removeItem(E item);

	void beforeStore();

	void loadEnumSet(Collection<EntityPartItem> enums);

}
