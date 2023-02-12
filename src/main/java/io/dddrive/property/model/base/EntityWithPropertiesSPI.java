package io.dddrive.property.model.base;

import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.property.model.Property;
import io.dddrive.property.model.PropertyProvider;

public interface EntityWithPropertiesSPI {

	PropertyProvider getPropertyProvider();

	Part<?> addPart(Property<?> property, CodePartListType partListType);

	void afterSet(Property<?> property);

	void afterAdd(Property<?> property);

	void afterRemove(Property<?> property);

	void afterClear(Property<?> property);

}
