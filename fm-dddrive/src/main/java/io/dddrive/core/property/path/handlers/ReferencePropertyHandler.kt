package io.dddrive.core.property.path.handlers

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.property.model.PartReferenceProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.core.property.path.PathElementHandler
import io.dddrive.core.property.path.PathHandlingContext
import io.dddrive.core.property.path.PathHandlingResult
import org.springframework.stereotype.Component

/**
 * Handles ReferenceProperty access - single object references
 * Delegates remaining path processing to the referenced entity
 */
@Component
class ReferencePropertyHandler : PathElementHandler {
	override fun canHandle(
		path: String,
		property: Property<*>?,
		entity: Any,
	): Boolean = property is ReferenceProperty<*> && property !is PartReferenceProperty<*>

	override fun handleSet(
		path: String,
		value: Any?,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		if (property !is ReferenceProperty<*>) {
			return PathHandlingResult.complete()
		}

		val segments = path.split('.', limit = 2)
		if (segments.size == 1) {
			// Direct access to the property
			@Suppress("UNCHECKED_CAST")
			(property as ReferenceProperty<Aggregate>).setValue(value as Aggregate?)
			return PathHandlingResult.complete()
		}

		// Traversal
		val referencedEntity =
			property.value
				?: throw IllegalArgumentException("Reference property is null, cannot access path: $path")

		val remainingPath = segments[1]
		return context.processRemainingPath(remainingPath, referencedEntity, value)
	}

	override fun handleGet(
		path: String,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		if (property !is ReferenceProperty<*>) {
			return PathHandlingResult.complete()
		}

		val segments = path.split('.', limit = 2)
		if (segments.size == 1) {
			// Direct access to the property
			return PathHandlingResult.complete(property.value)
		}

		// Traversal
		val referencedEntity = property.value
		if (referencedEntity == null) {
			return PathHandlingResult.complete(null)
		}

		val remainingPath = segments[1]
		return context.processRemainingPath(remainingPath, referencedEntity)
	}
}
