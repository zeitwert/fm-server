package dddrive.domain.ddd.persist.map

import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.BaseProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.ReferenceProperty
import dddrive.ddd.property.model.ReferenceSetProperty

/**
 * Serializes an entity with all its properties to a nested map structure.
 *
 * Property type mapping:
 * - BaseProperty<T> → value directly
 * - ReferenceProperty<T, ID> → stored as "propertyId" with the ID value
 * - EnumSetProperty<E> → Set<String> of enum IDs
 * - ReferenceSetProperty<A> → Set<Any> of aggregate IDs
 * - PartListProperty<A, P> → List<Map<String, Any?>> (recursive)
 */
fun EntityWithProperties.toMap(): Map<String, Any?> {
	val result = mutableMapOf<String, Any?>()

	for (property in properties) {
		when (property) {
			is PartListProperty<*, *> -> {
				result[property.name] = property.map { part -> part.toMap() }
			}

			is EnumSetProperty<*> -> {
				// Store actual enum objects (not IDs) since this is in-memory persistence
				result[property.name] = property.toSet()
			}

			is ReferenceSetProperty<*> -> {
				result[property.name] = property.toSet()
			}

			is ReferenceProperty<*, *> -> {
				// Store reference as "propertyId" to match convention
				result[property.name + "Id"] = property.id
			}

			is BaseProperty<*> -> {
				result[property.name] = property.value
			}
		}
	}

	// For parts, also include the id
	if (this is Part<*>) {
		result["id"] = this.id
	}

	return result
}

/**
 * Deserializes a map structure back into an entity.
 *
 * Uses two-pass deserialization:
 * 1. First pass: Create all parts (structure only)
 * 2. Second pass: Set all property values
 *
 * This ensures all parts exist before any part references are set (e.g., spouseId can reference
 * another member in the same list).
 */
fun EntityWithProperties.fromMap(map: Map<String, Any?>) {
	// Pass 1: Create all parts (structure only, depth-first)
	createPartsFromMap(map)

	// Pass 2: Set all property values
	setValuesFromMap(map)
}

/**
 * Pass 1: Recursively creates all parts from the map. Only handles PartListProperty - creates parts
 * with their IDs but doesn't set any values.
 */
private fun EntityWithProperties.createPartsFromMap(map: Map<String, Any?>) {
	for (property in properties) {
		if (property is PartListProperty<*, *>) {
			property.clear()
			val partMaps = map[property.name] as? List<Map<String, Any?>> ?: continue
			for (partMap in partMaps) {
				val partId = partMap["id"] as? Int
				val part = property.add(partId)
				// Recursively create nested parts
				part.createPartsFromMap(partMap)
			}
		}
	}
}

/**
 * Pass 2: Recursively sets all property values from the map. At this point, all parts already
 * exist, so part references will work.
 */
@Suppress("UNCHECKED_CAST")
private fun EntityWithProperties.setValuesFromMap(map: Map<String, Any?>) {
	for (property in properties) {
		when (property) {
			is PartListProperty<*, *> -> {
				// Recurse into parts to set their values
				val partMaps = map[property.name] as? List<Map<String, Any?>> ?: continue
				partMaps.forEachIndexed { idx, partMap -> property.get(idx).setValuesFromMap(partMap) }
			}

			is EnumSetProperty<*> -> {
				property.clear()
				val enumItems = map[property.name] as? Set<*> ?: continue
				for (item in enumItems) {
					if (item != null) {
						@Suppress("UNCHECKED_CAST")
						(property as EnumSetProperty<dddrive.ddd.enums.model.Enumerated>).add(
							item as dddrive.ddd.enums.model.Enumerated,
						)
					}
				}
			}

			is ReferenceSetProperty<*> -> {
				property.clear()
				val refIds = map[property.name] as? Set<Any> ?: continue
				for (refId in refIds) {
					property.add(refId)
				}
			}

			is ReferenceProperty<*, *> -> {
				// References are stored as "propertyNameId"
				val id = map[property.name + "Id"]
				(property as ReferenceProperty<Any, Any>).id = id
			}

			is BaseProperty<*> -> {
				val value = map[property.name]
				(property as BaseProperty<Any>).value = value
			}
		}
	}
}
