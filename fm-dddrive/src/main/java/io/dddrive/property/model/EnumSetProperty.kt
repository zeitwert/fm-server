package io.dddrive.property.model

import io.dddrive.enums.model.Enumerated

interface EnumSetProperty<E : Enumerated> : Property<E> {

	val items: Set<E>

	fun hasItem(item: E): Boolean

	fun clearItems()

	fun addItem(item: E)

	fun removeItem(item: E)

}
