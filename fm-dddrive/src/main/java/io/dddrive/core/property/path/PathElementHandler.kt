package io.dddrive.core.property.path

import io.dddrive.core.property.model.Property

/**
 * Handler for specific path element types in property path processing.
 *
 * Implementations handle specialized path patterns like:
 * - Enum properties with .id suffix (e.g., "status.id")
 * - List elements with indices (e.g., "items[0]")
 * - Complex property navigation
 *
 * Handlers are checked in registration order. Only one handler should claim
 * responsibility for any given path to avoid conflicts.
 */
interface PathElementHandler {

	/**
	 * Determines if this handler can process the given path segment and property.
	 *
	 * @param path the full remaining path (e.g., "birthDate.year", "civilStatus.id")
	 * @param property the property at the current path segment (may be null for list indices)
	 * @param entity the current entity being processed
	 * @return true if this handler should process this path element
	 */
	fun canHandle(
		path: String,
		property: Property<*>?,
		entity: Any,
	): Boolean

	/**
	 * Sets a value using this handler.
	 *
	 * @param path the full remaining path
	 * @param value the value to set
	 * @param property the property at the current path segment
	 * @param entity the current entity
	 * @param context processing context for delegation to other handlers
	 * @return PathHandlingResult indicating if processing is complete or should continue
	 */
	fun handleSet(
		path: String,
		value: Any?,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult

	/**
	 * Gets a value using this handler.
	 *
	 * @param path the full remaining path
	 * @param property the property at the current path segment
	 * @param entity the current entity
	 * @param context processing context for delegation to other handlers
	 * @return PathHandlingResult with the retrieved value
	 */
	fun handleGet(
		path: String,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult

}
