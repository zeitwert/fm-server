package dddrive.ddd.property.model.impl

import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.enums.model.Enumeration
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.EnumSetProperty
import dddrive.ddd.property.model.base.PropertyBase
import java.util.function.Consumer

class EnumSetPropertyImpl<E : Enumerated>(
	entity: EntityWithProperties,
	name: String,
	private val enumeration: Enumeration<E>,
) : PropertyBase<E>(entity, name),
	EnumSetProperty<E> {

	private val items: MutableSet<E> = mutableSetOf()

	override fun clear() {
		require(isWritable) { "writable" }
		items.forEach(Consumer { item: E -> remove(item) })
		items.clear()
		(entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	override fun add(item: E) {
		require(isWritable) { "writable" }
		require(isValidEnum(item)) { "valid enumeration item for " + enumeration.id + " (" + item.id + ")" }
		if (!has(item)) {
			val entity = entity as EntityWithPropertiesSPI
			entity.fireValueAddedChange(this, item.id)
			items.add(item)
			entity.doAfterAdd(this, null)
		}
	}

	override fun has(item: E): Boolean = items.contains(item)

	override fun remove(item: E) {
		require(isWritable) { "writable" }
		if (has(item)) {
			val entity = entity as EntityWithPropertiesSPI
			entity.fireValueRemovedChange(this, item.id)
			items.remove(item)
			entity.doAfterRemove(this)
		}
	}

	private fun isValidEnum(value: E): Boolean {
		if (value.enumeration != enumeration) {
			return false
		}
		return value == enumeration.getItem(value.id)
	}

	override val size: Int get() = items.size

	override fun isEmpty(): Boolean = items.isEmpty()

	override fun iterator(): Iterator<E> = items.iterator()

	override fun containsAll(elements: Collection<E>): Boolean = items.containsAll(elements)

	override fun contains(element: E): Boolean = items.contains(element)

}
