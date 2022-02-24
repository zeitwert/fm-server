package io.zeitwert.ddd.property.model;

import java.util.List;
import java.util.Map;

public interface EntityWithProperties {

	boolean hasProperty(String name);

	Property<?> getProperty(String name);

	Map<String, Property<?>> getPropertyMap();

	List<Property<?>> getPropertyList();

}
