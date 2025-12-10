package io.dddrive.core.ddd.model;

import io.dddrive.core.oe.model.ObjTenant;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.EntityWithProperties;

/**
 * A DDD Aggregate Root.
 */
public interface Aggregate extends EntityWithProperties {

	Object getId();

	Object getTenantId();

	ObjTenant getTenant();

	ObjUser getOwner();

	void setOwner(ObjUser owner);

	String getCaption();

	AggregateMeta getMeta();

	/**
	 * Sets the value of a property identified by its canonical path relative to this aggregate.
	 *
	 * @param <T>          the type of the value
	 * @param relativePath the dot-separated path to the property.
	 *                     For parts in a list, use bracket-notation with the 0-based index (e.g., `partList[0].name`).
	 *                     To set a reference property, append `.id` to the property name and provide the ID as the value (e.g., `assignee.id`).
	 *                     Example: "directPropName", "partRefProp.nameOnPart", "partList[0].nameOnPart", "assignee.id"
	 * @param value        the value to set
	 * @throws IllegalArgumentException if the path is invalid, a segment is not found, or the property is not settable.
	 * @throws IllegalStateException    if a referenced entity or part in the path is null (and the path is not setting an ID).
	 */
	<T> void setValueByPath(String relativePath, T value);

	/**
	 * Calculate all the derived fields, typically after a field change.
	 */
	void calcAll();

	/**
	 * Calculate all the volatile derived fields, i.e. fields that are not saved to
	 * the database. This is triggered after loading the aggregate from the
	 * database.
	 */
	void calcVolatile();

}
