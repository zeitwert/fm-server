package io.dddrive.core.property.model;

import java.util.List;

import io.dddrive.core.ddd.model.Part;

public interface EntityWithProperties {

	boolean isFrozen();

	boolean hasProperty(String name);

	Property<?> getProperty(String name);

	List<Property<?>> getProperties();

	boolean hasPart(Integer partId);

	Part<?> getPart(Integer partId);

	/**
	 * Sets the value of a property identified by its canonical path relative to this entity.
	 *
	 * @param <T>          the type of the value
	 * @param relativePath the dot-separated path to the property.
	 *                     For parts in a list, the segment following the list property name should be the part's integer ID or its 0-based index.
	 *                     Example: "directPropName", "partRefProp.nameOnPart", "partListProp.123.nameOnPart", "partListProp.0.nameOnPart"
	 * @param value        the value to set
	 * @throws IllegalArgumentException if the path is invalid, a segment is not found, or the property is not settable.
	 * @throws IllegalStateException    if a referenced part in the path is null.
	 */
	<T> void setValueByPath(String relativePath, T value);

	<T> Property<T> getPropertyByPath(String relativePath);

}
