package io.dddrive.core.property.model

import io.dddrive.core.ddd.model.Aggregate

interface ReferenceSetProperty<A : Aggregate> : Property<A> {

	val items: Set<Any>

	fun hasItem(aggregateId: Any): Boolean

	fun clearItems()

	fun addItem(aggregateId: Any)

	fun removeItem(aggregateId: Any)

}
