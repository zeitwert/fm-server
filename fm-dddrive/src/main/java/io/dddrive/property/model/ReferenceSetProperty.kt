package io.dddrive.property.model

import io.dddrive.ddd.model.Aggregate

interface ReferenceSetProperty<A : Aggregate> : Property<A> {

	val items: Set<Any>

	fun hasItem(aggregateId: Any): Boolean

	fun clearItems()

	fun addItem(aggregateId: Any)

	fun removeItem(aggregateId: Any)

}
