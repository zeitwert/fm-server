package io.dddrive.property.model;

import java.util.List;

public interface EntityWithProperties {

	boolean hasProperty(String name);

	Property<?> getProperty(String name);

	List<Property<?>> getProperties();

}
