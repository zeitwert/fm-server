package dddrive.ddd.path

import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import dddrive.ddd.property.model.ReferenceProperty

/**
 * Resolves a property by path.
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
 *
 * @param path The property path (e.g., "children[0].status.id")
 * @return The resolved property, or null if the path navigates through null reference
 */
fun <T : Any> EntityWithProperties.getPropertyByPath(path: String): Property<T>? = resolveProperty<T>(path, forSetter = false)

/**
 * Gets a value by path.
 *
 * @param path The property path (e.g., "children[0].name")
 * @return The value, or null if the path navigates through null reference or missing list index
 */
fun <T : Any> EntityWithProperties.getValueByPath(path: String): T? = (resolveProperty<T>(path, forSetter = false) as? BaseProperty<T>)?.value

/**
 * Sets a value by path.
 *
 * Lists are auto-expanded to accommodate the index.
 * Crashes if navigating through a null reference.
 *
 * @param path The property path (e.g., "children[0].name")
 * @param value The value to set
 */
fun <T : Any> EntityWithProperties.setValueByPath(
	path: String,
	value: T?,
) {
	val property = resolveProperty<T>(path, forSetter = true) ?: error("Could not resolve path: $path")
	@Suppress("UNCHECKED_CAST")
	(property as BaseProperty<T>).value = value
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> EntityWithProperties.resolveProperty(
	path: String,
	index: Int = 0,
	forSetter: Boolean = false,
): Property<T>? {
	var entity: EntityWithProperties = this
	var i = index

	val segments = path.replace("[", ".").replace("]", "").split(".")
	while (i < segments.size) {
		val segment = segments[i]
		val isLast = (i == segments.size - 1)
		val nextSegment = segments.getOrNull(i + 1)

		// Check for "propId" suffix (e.g., "referenceId" or "_tenantId")
		// Only if no literal "somethingId" property exists
		if (isLast && segment.endsWith("Id") && segment.length > 2 && !entity.hasProperty(segment) && !entity.hasProperty("_$segment")) {
			val baseName = segment.removeSuffix("Id")
			// Try baseName directly, then with underscore prefix
			val refPropertyName = when {
				entity.hasProperty(baseName) -> baseName
				entity.hasProperty("_$baseName") -> "_$baseName"
				else -> null
			}
			if (refPropertyName != null) {
				val baseProperty = entity.getProperty(refPropertyName, Any::class)
				if (baseProperty is ReferenceProperty<*, *>) {
					return baseProperty.idProperty as Property<T>
				}
			}
		}

		// Try the segment name directly, then fall back to underscore-prefixed (Kotlin style)
		val property = if (entity.hasProperty(segment)) {
			entity.getProperty(segment, Any::class)
		} else if (entity.hasProperty("_$segment")) {
			entity.getProperty("_$segment", Any::class)
		} else {
			entity.getProperty(segment, Any::class) // Let it throw with original name
		}

		when {
			isLast -> {
				return property as Property<T>
			}

			// .id suffix - getter only
			nextSegment == "id" && property is ReferenceProperty<*, *> -> {
				if (forSetter) error("Use '${segment}Id' for setting, not '$segment.id'")
				require(i + 2 == segments.size) { "Cannot navigate past .id" }
				return property.idProperty as Property<T>
			}

			// Numeric - list index
			nextSegment?.toIntOrNull() != null -> {
				require(property is PartListProperty<*>) { "$segment is not a list" }
				val idx = nextSegment.toInt()
				if (forSetter) {
					while (property.size <= idx) property.add(null)
				} else if (idx >= property.size) {
					return null
				}
				entity = property.get(idx)
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

			else -> {
				error("Cannot navigate through $segment")
			}
		}
	}
	return null
}
