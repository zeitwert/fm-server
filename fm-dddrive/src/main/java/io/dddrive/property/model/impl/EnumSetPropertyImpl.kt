package io.dddrive.property.model.impl

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.Enumeration
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.base.PropertyBase
import java.util.function.Consumer

class EnumSetPropertyImpl<E : Enumerated>(
	entity: EntityWithProperties,
	name: String,
	private val enumeration: Enumeration<E>,
) : PropertyBase<E>(entity, name),
	EnumSetProperty<E> {

	private val itemSet: MutableSet<E> = mutableSetOf()

	override fun clearItems() {
		require(this.isWritable) { "writable" }
		this.itemSet.forEach(Consumer { item: E -> this.removeItem(item) })
		this.itemSet.clear()
		(this.entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	override fun addItem(item: E) {
		require(this.isWritable) { "writable" }
		require(this.isValidEnum(item)) { "valid enumeration item for " + this.enumeration.id + " (" + item.id + ")" }
		if (!this.hasItem(item)) {
			val entity = this.entity as EntityWithPropertiesSPI
			entity.fireValueAddedChange(this, item.id)
			this.itemSet.add(item)
			entity.doAfterAdd(this, null)
		}
	}

	override val items: Set<E>
		get() = this.itemSet.toSet()

	override fun hasItem(item: E): Boolean = this.itemSet.contains(item)

	override fun removeItem(item: E) {
		require(this.isWritable) { "writable" }
		if (this.hasItem(item)) {
			val entity = this.entity as EntityWithPropertiesSPI
			entity.fireValueRemovedChange(this, item.id)
			this.itemSet.remove(item)
			entity.doAfterRemove(this)
		}
	}

	private fun isValidEnum(value: E): Boolean {
		if (value.enumeration != this.enumeration) {
			return false
		}
		return value == this.enumeration.getItem(value.id)
	}

}
