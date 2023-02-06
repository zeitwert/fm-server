package io.zeitwert.ddd.property.model.base;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.PropertyProvider;

public interface EntityWithPropertiesSPI {

	AppContext getAppContext();

	PropertyProvider getPropertyProvider();

	Part<?> addPart(Property<?> property, CodePartListType partListType);

	void afterSet(Property<?> property);

	void afterAdd(Property<?> property);

	void afterRemove(Property<?> property);

	void afterClear(Property<?> property);

}
