package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.part.model.enums.CodePartListType;

public interface CollectionProperty<T> extends Property<T> {

	CodePartListType getPartListType();

}
