package io.dddrive.property.model;

import io.dddrive.ddd.model.enums.CodePartListType;

public interface CollectionProperty<T> extends Property<T> {

	CodePartListType getPartListType();

}
