package io.dddrive.core.property.path

import io.dddrive.core.property.model.Property
import io.dddrive.core.property.path.handlers.DefaultPropertyHandler

/**
 * Registry for managing PathElementHandler instances.
 * Allows runtime registration and removal of handlers for extensibility.
 */
class PathElementHandlerRegistry {

	private val handlers = mutableListOf<PathElementHandler>()

	/**
	 * Register a new handler. Handlers are checked in registration order.
	 * @param handler the handler to register
	 */
	fun registerHandler(handler: PathElementHandler) {
		handlers.add(handler)
	}

	/**
	 * Remove a handler from the registry
	 * @param handler the handler to remove
	 * @return true if the handler was found and removed
	 */
	fun removeHandler(handler: PathElementHandler): Boolean = handlers.remove(handler)

	/**
	 * Find the appropriate handler for the given path, property, and entity.
	 *
	 * Uses a two-phase search strategy:
	 * 1. Check all specific handlers (non-DefaultPropertyHandler) first
	 * 2. Fall back to DefaultPropertyHandler if no specific handler matches
	 *
	 * @param path the full remaining path
	 * @param property the property at the current path segment (may be null)
	 * @param entity the current entity
	 * @return the matching handler or null if none found
	 * @throws IllegalStateException if multiple specific handlers claim the same path
	 */
	fun findHandler(
		path: String,
		property: Property<*>?,
		entity: Any,
	): PathElementHandler? {
		val specificHandlers = handlers.filter { it !is DefaultPropertyHandler && it.canHandle(path, property, entity) }

		if (specificHandlers.size > 1) {
			throw IllegalStateException(
				"Multiple specific handlers found for path: $path. " +
					"Conflicting handlers: ${specificHandlers.joinToString { it.javaClass.simpleName }}",
			)
		}

		if (specificHandlers.isNotEmpty()) {
			return specificHandlers.first()
		}

		val defaultHandler = handlers.find { it is DefaultPropertyHandler }
		if (defaultHandler != null && defaultHandler.canHandle(path, property, entity)) {
			return defaultHandler
		}

		return null
	}

	/**
	 * Get a copy of all registered handlers
	 * @return list of all handlers in registration order
	 */
	fun getHandlers(): List<PathElementHandler> = handlers.toList()

	/**
	 * Clear all registered handlers
	 */
	fun clear() {
		handlers.clear()
	}
}
