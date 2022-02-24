package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.property.model.enums.CodePartListType;

public interface CollectionProperty<T> extends Property<T> {

	CodePartListType getPartListType();

}
