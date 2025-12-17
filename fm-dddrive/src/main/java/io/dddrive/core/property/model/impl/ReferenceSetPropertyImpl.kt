package io.dddrive.core.property.model.impl

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EntityWithPropertiesSPI
import io.dddrive.core.property.model.ReferenceSetProperty
import io.dddrive.core.property.model.base.PropertyBase
import io.dddrive.util.Invariant
import java.util.function.Consumer

class ReferenceSetPropertyImpl<A : Aggregate>(
	entity: EntityWithProperties,
	name: String,
	repository: AggregateResolver<A>,
) : PropertyBase<A>(entity, name),
	ReferenceSetProperty<A> {

	private val itemSet: MutableSet<Any> = mutableSetOf()

	override fun clearItems() {
		Invariant.requireThis(this.isWritable, "not frozen")
		this.itemSet.forEach(Consumer { aggregateId: Any? -> this.removeItem(aggregateId!!) })
		this.itemSet.clear()
		(this.entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	override fun addItem(aggregateId: Any) {
		Invariant.requireThis(this.isWritable, "not frozen")
		if (this.hasItem(aggregateId)) {
			return
		}
		Invariant.assertThis(this.isValidAggregateId(aggregateId), "valid aggregate id [$aggregateId]")
		if (!this.hasItem(aggregateId)) {
			val entity = this.entity as EntityWithPropertiesSPI
			entity.fireValueAddedChange(this, aggregateId)
			this.itemSet.add(aggregateId)
			entity.doAfterAdd(this, null)
		}
	}

	override val items: Set<Any>
		get() = this.itemSet.toSet()

	override fun hasItem(aggregateId: Any): Boolean = this.itemSet.contains(aggregateId)

	override fun removeItem(aggregateId: Any) {
		Invariant.requireThis(this.isWritable, "not frozen")
		if (this.hasItem(aggregateId)) {
			val entity = this.entity as EntityWithPropertiesSPI
			entity.fireValueRemovedChange(this, aggregateId)
			this.itemSet.remove(aggregateId)
			entity.doAfterRemove(this)
		}
	}

	// TODO too expensive?
	private fun isValidAggregateId(id: Any?): Boolean = true

}
