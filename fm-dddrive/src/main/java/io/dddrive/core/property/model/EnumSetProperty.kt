package io.dddrive.core.property.model

import io.dddrive.core.enums.model.Enumerated

interface EnumSetProperty<E : Enumerated> : Property<E> {

	val items: Set<E>

	fun hasItem(item: E): Boolean

	fun clearItems()

	fun addItem(item: E)

	fun removeItem(item: E)

}
