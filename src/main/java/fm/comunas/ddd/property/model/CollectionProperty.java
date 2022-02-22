package fm.comunas.ddd.property.model;

import fm.comunas.ddd.property.model.enums.CodePartListType;

public interface CollectionProperty<T> extends Property<T> {

	CodePartListType getPartListType();

}
