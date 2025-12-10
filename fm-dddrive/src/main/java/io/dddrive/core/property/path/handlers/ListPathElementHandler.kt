package io.dddrive.core.property.path.handlers

import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.path.PathElementHandler
import io.dddrive.core.property.path.PathHandlingContext
import io.dddrive.core.property.path.PathHandlingResult
import org.springframework.stereotype.Component

/**
 * Handles list property access with indexed notation like partnerList[1]
 * Automatically expands lists during SET operations when accessing indices that don't exist yet.
 * For GET operations, returns null if the index doesn't exist (read-only behavior).
 */
@Component
class ListPathElementHandler : PathElementHandler {
	private val listIndexRegex = Regex("""^(\w+)\[(\d+)](?:\.(.+))?$""")

	override fun canHandle(
		path: String,
		property: Property<*>?,
		entity: Any,
	): Boolean {
		val match = listIndexRegex.find(path)
		if (match == null) return false

		val (listName) = match.destructured

		// Check if entity has properties and the list property exists
		if (entity !is EntityWithProperties) return false

		return try {
			val listProperty = entity.getProperty(listName)
			listProperty is io.dddrive.core.property.model.PartListProperty<*>
		} catch (_: Exception) {
			false
		}
	}

	override fun handleSet(
		path: String,
		value: Any?,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		val match =
			listIndexRegex.find(path)
				?: return PathHandlingResult.complete()

		val (listName, indexStr, remainingPath) = match.destructured
		val index = indexStr.toInt()

		if (entity !is EntityWithProperties) {
			throw IllegalArgumentException("Entity $entity does not support properties")
		}

		val listProperty = entity.getProperty(listName) as io.dddrive.core.property.model.PartListProperty<*>

		// Ensure the list has enough elements
		expandListToIndex(listProperty, index)

		val listElement = listProperty.getPart(index)

		return if (remainingPath.isEmpty()) {
			// Direct assignment to list element (rare case)
			throw IllegalArgumentException("Cannot directly assign to list element at path: $path")
		} else {
			// Continue processing with the remaining path on the list element
			context.processRemainingPath(remainingPath, listElement, value)
		}
	}

	override fun handleGet(
		path: String,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		val match =
			listIndexRegex.find(path)
				?: return PathHandlingResult.complete()

		val (listName, indexStr, remainingPath) = match.destructured
		val index = indexStr.toInt()

		if (entity !is EntityWithProperties) {
			throw IllegalArgumentException("Entity $entity does not support properties")
		}

		val listProperty = entity.getProperty(listName) as io.dddrive.core.property.model.PartListProperty<*>

		// For GET operations, do NOT expand the list - return null if index doesn't exist
		if (index >= listProperty.partCount) {
			return PathHandlingResult.complete(null)
		}

		val listElement = listProperty.getPart(index)

		return if (remainingPath.isEmpty()) {
			// Return the list element itself (rare case)
			PathHandlingResult.complete(listElement)
		} else {
			// Continue processing with the remaining path on the list element
			context.processRemainingPath(remainingPath, listElement)
		}
	}

	private fun expandListToIndex(
		listProperty: io.dddrive.core.property.model.PartListProperty<*>,
		targetIndex: Int,
	) {
		val currentSize = listProperty.partCount
		when {
			currentSize > targetIndex -> {
				// Index already exists, nothing to do
			}
			else -> {
				// Need to expand list to accommodate target index
				// Add null elements until we reach the target index
				repeat(targetIndex - currentSize + 1) {
					listProperty.addPart(null)
				}
			}
		}
	}
}
