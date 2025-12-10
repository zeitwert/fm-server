package io.dddrive.core.property.path

import io.dddrive.core.property.model.Property

/**
 * Context for path processing operations, enabling handlers to delegate
 * remaining path segments to the appropriate handlers.
 *
 * This interface allows specialized handlers to process their specific path
 * segment and then delegate the rest of the path to the processing framework.
 */
interface PathHandlingContext {
	/**
	 * Process the remaining path on the given entity.
	 *
	 * This method re-enters the path processing framework to handle the
	 * remaining path segments using the appropriate handlers.
	 *
	 * @param path remaining path to process
	 * @param entity entity to process the path on
	 * @param value value to set (for set operations, null for get operations)
	 * @return result of processing
	 */
	fun processRemainingPath(
		path: String,
		entity: Any,
		value: Any? = null,
	): PathHandlingResult

	/**
	 * Get a property by path from the given entity.
	 *
	 * @param entity entity to get property from
	 * @param path path to the property
	 * @return the property or null if not found
	 */
	fun getPropertyByPath(
		entity: Any,
		path: String,
	): Property<*>?
}
