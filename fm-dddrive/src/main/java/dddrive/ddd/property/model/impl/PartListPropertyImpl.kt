package dddrive.ddd.property.model.impl

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.PartSPI
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.base.PropertyBase

open class PartListPropertyImpl<A : Aggregate, P : Part<A>>(
	entity: EntityWithProperties,
	name: String,
	aggrType: Class<A>,
	override val partType: Class<P>,
) : PropertyBase<P>(entity, name),
	PartListProperty<A, P> {

	private val parts: MutableList<P> = mutableListOf()

	override fun clear() {
		require(isWritable) { "writable" }
		for (part in parts) {
			(part as PartSPI<*>).delete()
		}
		parts.clear()
		(entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	@Suppress("UNCHECKED_CAST")
	override fun add(partId: Int?): P {
		require(isWritable) { "writable" }
		val entity = entity as EntityWithPropertiesSPI
		val part = entity.doAddPart(this, partId) as P
		parts.add(part)
		(part as EntityWithPropertiesSPI).fireEntityAddedChange(part.id)
		entity.doAfterAdd(this, part)
		return part
	}

	override val size: Int
		get() = parts.size

	override fun get(seqNr: Int): P {
		require(seqNr in 0..<size) { "valid seqNr (0 <= $seqNr < $size)" }
		return parts[seqNr]
	}

	override fun getById(partId: Int): P = parts.first { it.id == partId }

	override fun remove(partId: Int) {
		require(isWritable) { "writable" }
		remove(getById(partId))
	}

	override fun remove(part: P) {
		require(isWritable) { "writable" }
		(part as EntityWithPropertiesSPI).fireEntityRemovedChange()
		(part as PartSPI<*>).delete()
		parts.remove(part)
		(entity as EntityWithPropertiesSPI).doAfterRemove(this)
	}

	override fun indexOf(part: P): Int = parts.indexOf(part)

	override fun isEmpty(): Boolean = parts.isEmpty()

	override fun contains(element: P): Boolean = parts.contains(element)

	override fun containsAll(elements: Collection<P>): Boolean = parts.containsAll(elements)

	override fun iterator(): Iterator<P> = parts.iterator()

}
