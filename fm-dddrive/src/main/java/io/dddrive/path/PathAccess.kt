package io.dddrive.path

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty

/**
 * Extension functions for path-based property access on entities.
 *
 * Path syntax:
 * - Simple property: "name"
 * - Nested via reference: "reference.name"
 * - List access: "children[0].name" or "children.0.name"
 * - Reference ID: "referenceId" (for both get/set) or "reference.id" (get only)
 *
 * Null handling:
 * - Getter: Returns null when navigating through null reference (?. semantics)
 * - Setter: Crashes when navigating through null reference
 * - List expansion: Setter auto-expands lists to accommodate index
 */

private fun parsePath(path: String): List<String> =
	path.replace("[", ".").replace("]", "").split(".")

private fun EntityWithProperties.resolveProperty(
	segments: List<String>,
	index: Int = 0,
	forSetter: Boolean = false,
): Property<*>? {
	var entity: EntityWithProperties = this
	var i = index

	while (i < segments.size) {
		val segment = segments[i]
		val isLast = (i == segments.size - 1)
		val nextSegment = segments.getOrNull(i + 1)

		// Check for "propId" suffix (e.g., "referenceId")
		// Only if no literal "somethingId" property exists
		if (isLast && segment.endsWith("Id") && segment.length > 2 && !entity.hasProperty(segment)) {
			val baseName = segment.removeSuffix("Id")
			if (entity.hasProperty(baseName)) {
				val baseProperty = entity.getProperty(baseName)
				if (baseProperty is ReferenceProperty<*, *>) {
					return baseProperty.idProperty
				}
			}
		}

		val property = entity.getProperty(segment)

		when {
			isLast -> return property

			// .id suffix - getter only
			nextSegment == "id" && property is ReferenceProperty<*, *> -> {
				if (forSetter) error("Use '${segment}Id' for setting, not '$segment.id'")
				require(i + 2 == segments.size) { "Cannot navigate past .id" }
				return property.idProperty
			}

			// Numeric - list index
			nextSegment?.toIntOrNull() != null -> {
				require(property is PartListProperty<*>) { "$segment is not a list" }
				val idx = nextSegment.toInt()
				if (forSetter) {
					while (property.partCount <= idx) property.addPart(null)
				} else if (idx >= property.partCount) {
					return null
				}
				entity = property.getPart(idx)
				i += 2
				continue
			}

			// Reference navigation
			property is ReferenceProperty<*, *> -> {
				val referenced = property.value
				if (referenced == null) {
					if (forSetter) error("Cannot set through null reference: $segment")
					return null // Getter: ?. semantics
				}
				entity = referenced as EntityWithProperties
				i += 1
				continue
			}

			else -> error("Cannot navigate through $segment")
		}
	}
	return null
}

/**
 * Resolves a property by path.
 *
 * @param path The property path (e.g., "children[0].status.id")
 * @return The resolved property, or null if path navigates through null reference
 */
fun EntityWithProperties.getPropertyByPath(path: String): Property<*>? =
	resolveProperty(parsePath(path), forSetter = false)

/**
 * Gets a value by path.
 *
 * @param path The property path (e.g., "children[0].name")
 * @return The value, or null if path navigates through null reference or missing list index
 */
fun EntityWithProperties.getValueByPath(path: String): Any? =
	(resolveProperty(parsePath(path), forSetter = false) as? BaseProperty<*>)?.value

/**
 * Sets a value by path.
 *
 * Lists are auto-expanded to accommodate the index.
 * Crashes if navigating through a null reference.
 *
 * @param path The property path (e.g., "children[0].name")
 * @param value The value to set
 */
fun <T> EntityWithProperties.setValueByPath(path: String, value: T?) {
	val property = resolveProperty(parsePath(path), forSetter = true)
		?: error("Could not resolve path: $path")
	@Suppress("UNCHECKED_CAST")
	(property as BaseProperty<T>).value = value
}
