package io.dddrive.core.property.model.impl

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.core.property.model.EnumSetProperty
import io.dddrive.core.property.model.base.PropertyBase
import io.dddrive.util.Invariant
import io.dddrive.util.Invariant.MessageProvider
import java.util.function.Consumer

class EnumSetPropertyImpl<E : Enumerated>(
	entity: EntityWithProperties,
	name: String,
	private val enumeration: Enumeration<E>,
) : PropertyBase<E>(entity, name),
	EnumSetProperty<E> {

	private val itemSet: MutableSet<E> = mutableSetOf()

	override fun clearItems() {
		Invariant.requireThis(this.isWritable, "not frozen")
		this.itemSet.forEach(Consumer { item: E -> this.removeItem(item) })
		this.itemSet.clear()
		(this.entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	override fun addItem(item: E) {
		Invariant.requireThis(this.isWritable, "not frozen")
		println("addItem: $item ${item.id} ${item.enumeration} ${item.enumeration.id}")
		Invariant.assertThis(
			this.isValidEnum(item),
			MessageProvider { "valid enumeration item for " + this.enumeration.id + " (" + item.id + ")" },
		)
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
		Invariant.requireThis(this.isWritable, "not frozen")
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
