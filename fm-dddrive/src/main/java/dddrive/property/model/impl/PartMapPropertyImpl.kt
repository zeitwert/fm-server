package dddrive.property.model.impl

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part
import dddrive.ddd.model.PartSPI
import dddrive.property.model.EntityWithProperties
import dddrive.property.model.EntityWithPropertiesSPI
import dddrive.property.model.PartMapProperty
import dddrive.property.model.base.PropertyBase

open class PartMapPropertyImpl<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	name: String,
	override val partType: Class<P>,
) : PropertyBase<P>(entity, name),
	PartMapProperty<A, P> {

	private val parts: MutableMap<String, P> = mutableMapOf()

	override fun clear() {
		require(isWritable) { "writable" }
		val keysToRemove = parts.keys.toList()
		for (key in keysToRemove) {
			remove(key)
		}
		check(parts.isEmpty()) { "parts empty" }
		(entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	@Suppress("UNCHECKED_CAST")
	override fun add(
		key: String,
		partId: Int?,
	): P {
		require(isWritable) { "writable" }
		require(!parts.containsKey(key)) { "key '$key' already exists" }
		val part = (entity as EntityWithPropertiesSPI).doAddPart(this, partId) as P
		parts[key] = part
		firePartAddedChange(part as EntityWithPropertiesSPI)
		entity.doAfterAdd(this, part)
		return part
	}

	override val size: Int
		get() = parts.size

	override fun get(key: String): P {
		require(parts.containsKey(key)) { "key '$key' not found" }
		return parts[key]!!
	}

	override fun containsKey(key: String): Boolean = parts.containsKey(key)

	override fun remove(key: String) {
		require(isWritable) { "writable" }
		require(parts.containsKey(key)) { "key '$key' not found" }
		remove(parts[key]!!)
	}

	override fun remove(part: P) {
		require(isWritable) { "writable" }
		firePartRemovedChange(part as EntityWithPropertiesSPI)
		(part as PartSPI<*>).delete()
		val key = keyOf(part)
		parts.remove(key)
		(entity as EntityWithPropertiesSPI).doAfterRemove(this)
	}

	override fun keyOf(part: Part<*>): String {
		for ((key, value) in parts) {
			if (value === part) {
				return key
			}
		}
		error("Part not found in map")
	}

	override fun isEmpty(): Boolean = parts.isEmpty()

	override fun contains(element: P): Boolean = parts.containsValue(element)

	override fun containsAll(elements: Collection<P>): Boolean = parts.values.containsAll(elements)

	override fun iterator(): Iterator<P> = parts.values.iterator()

	// Map interface implementations
	override val entries: Set<Map.Entry<String, P>>
		get() = parts.entries

	override val keys: Set<String>
		get() = parts.keys

	override val values: Collection<P>
		get() = parts.values

	override fun containsValue(value: P): Boolean = parts.containsValue(value)

	override fun toString(): String = "$name: $parts"

}
