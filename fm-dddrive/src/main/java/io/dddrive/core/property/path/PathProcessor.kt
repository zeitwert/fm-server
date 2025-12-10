package io.dddrive.core.property.path

import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.Property
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Central processor for path-based field access using pluggable handlers
 * Supports extensible handler registration for different path element types
 */
@Component
class PathProcessor
	@Autowired
	constructor(
		handlers: List<PathElementHandler>,
	) : PathHandlingContext {
		private val handlerRegistry = PathElementHandlerRegistry()

		init {
			// Register all injected handlers
			handlers.forEach { handler ->
				handlerRegistry.registerHandler(handler)
			}
		}

		/**
		 * Set a field value by path
		 * @param path the field path (e.g., "partnerList[0].birthDate.year")
		 * @param value the value to set
		 * @param rootEntity the root entity to start navigation from
		 */
		fun setFieldByPath(
			path: String,
			value: Any?,
			rootEntity: Any,
		) {
			processPath(path, rootEntity, value, isSet = true)
		}

		/**
		 * Get a field value by path
		 * @param path the field path (e.g., "partnerList[0].birthDate.year")
		 * @param rootEntity the root entity to start navigation from
		 * @return the field value or null if not found
		 */
		fun getValueByPath(
			path: String,
			rootEntity: Any,
		): Any? {
			val result = processPath(path, rootEntity, null, isSet = false)
			return result.value
		}

		// PathHandlingContext implementation
		override fun processRemainingPath(
			path: String,
			entity: Any,
			value: Any?,
		): PathHandlingResult = processPath(path, entity, value, value != null)

		override fun getPropertyByPath(
			entity: Any,
			path: String,
		): Property<*>? =
			when (entity) {
				is EntityWithProperties -> entity.getPropertyByPath<Any>(path) as? Property<*>
				else -> throw IllegalArgumentException("Entity $entity does not support property access")
			}

		private fun processPath(
			path: String,
			entity: Any,
			value: Any?,
			isSet: Boolean,
		): PathHandlingResult {
			if (path.isEmpty()) {
				return PathHandlingResult.complete(entity)
			}

			// Extract the first segment of the path
			val firstSegment = extractFirstSegment(path)
			val property = getPropertyForSegment(firstSegment, entity)

			// Find an appropriate handler
			val handler =
				handlerRegistry.findHandler(path, property, entity)
					?: throw IllegalArgumentException("No handler found for path: $path on entity: ${entity.javaClass.simpleName}")

			// Delegate to the handler
			val result =
				if (isSet) {
					handler.handleSet(path, value, property, entity, this)
				} else {
					handler.handleGet(path, property, entity, this)
				}

			// Handle continuation if needed
			return if (!result.isComplete && result.continueWithPath != null && result.continueWithEntity != null) {
				processPath(result.continueWithPath, result.continueWithEntity, value, isSet)
			} else {
				result
			}
		}

		private fun extractFirstSegment(path: String): String {
			// Handle list indices: partnerList[0] -> partnerList
			val listMatch = Regex("""^(\w+)\[\d+]""").find(path)
			if (listMatch != null) {
				return listMatch.groupValues[1]
			}

			// Handle simple property: birthDate.year -> birthDate
			return path.split('.')[0]
		}

		private fun getPropertyForSegment(
			segment: String,
			entity: Any,
		): Property<*>? =
			try {
				when (entity) {
					is EntityWithProperties -> entity.getProperty(segment)
					else -> null
				}
			} catch (_: Exception) {
				null
			}

		/**
		 * Get all registered handlers for testing and inspection
		 * @return list of all handlers in registration order
		 */
		fun getRegisteredHandlers(): List<PathElementHandler> = handlerRegistry.getHandlers()
	}
