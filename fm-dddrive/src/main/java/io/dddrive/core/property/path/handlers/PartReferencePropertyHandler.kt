package io.dddrive.core.property.path.handlers

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.PartReferenceProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.path.PathElementHandler
import io.dddrive.core.property.path.PathHandlingContext
import io.dddrive.core.property.path.PathHandlingResult
import org.springframework.stereotype.Component

/**
 * Handles PartReferenceProperty access - part object references
 * Delegates remaining path processing to the referenced part entity
 */
@Component
class PartReferencePropertyHandler : PathElementHandler {
	override fun canHandle(
		path: String,
		property: Property<*>?,
		entity: Any,
	): Boolean = property is PartReferenceProperty<*>

	override fun handleSet(
		path: String,
		value: Any?,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		if (property !is PartReferenceProperty<*>) {
			return PathHandlingResult.complete()
		}

		val segments = path.split('.', limit = 2)
		if (segments.size == 1) {
			// Direct access to the property
			@Suppress("UNCHECKED_CAST")
			(property as PartReferenceProperty<Part<*>>).setValue(value as Part<*>?)
			return PathHandlingResult.complete()
		}

		// Traversal
		val referencedPart =
			property.value
				?: throw IllegalArgumentException("Part reference property is null, cannot access path: $path")

		val remainingPath = segments[1]
		return context.processRemainingPath(remainingPath, referencedPart, value)
	}

	override fun handleGet(
		path: String,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		if (property !is PartReferenceProperty<*>) {
			return PathHandlingResult.complete()
		}

		val segments = path.split('.', limit = 2)
		if (segments.size == 1) {
			// Direct access to the property
			return PathHandlingResult.complete(property.value)
		}

		// Traversal
		val referencedPart = property.value
		if (referencedPart == null) {
			return PathHandlingResult.complete(null)
		}

		val remainingPath = segments[1]
		return context.processRemainingPath(remainingPath, referencedPart)
	}
}
