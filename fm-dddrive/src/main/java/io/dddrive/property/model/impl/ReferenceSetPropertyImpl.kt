package io.dddrive.property.model.impl

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregateRepository
import io.dddrive.ddd.model.AggregateRepositorySPI
import io.dddrive.property.model.EntityWithProperties
import io.dddrive.property.model.EntityWithPropertiesSPI
import io.dddrive.property.model.ReferenceSetProperty
import io.dddrive.property.model.base.PropertyBase

class ReferenceSetPropertyImpl<A : Aggregate>(
	entity: EntityWithProperties,
	name: String,
	val repo: AggregateRepository<A>,
) : PropertyBase<A>(entity, name),
	ReferenceSetProperty<A> {

	private val itemSet: MutableSet<Any> = mutableSetOf()

	override fun clearItems() {
		require(isWritable) { "writable" }
		items.forEach { removeItem(it) }
		check(itemSet.isEmpty()) { "all items removed" }
		itemSet.clear()
		(entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	override fun addItem(aggregateId: Any) {
		require(isWritable) { "writable" }
		require(isValidAggregateId(aggregateId)) { "valid aggregate id [$aggregateId]" }
		if (hasItem(aggregateId)) {
			return
		}
		if (!hasItem(aggregateId)) {
			val entity = entity as EntityWithPropertiesSPI
			entity.fireValueAddedChange(this, aggregateId)
			itemSet.add(aggregateId)
			entity.doAfterAdd(this, null)
		}
	}

	override val items: Set<Any>
		get() = itemSet.toSet()

	override fun hasItem(aggregateId: Any): Boolean = itemSet.contains(aggregateId)

	override fun removeItem(aggregateId: Any) {
		require(isWritable) { "writable" }
		require(isValidAggregateId(aggregateId)) { "valid aggregate id [$aggregateId]" }
		if (hasItem(aggregateId)) {
			val entity = entity as EntityWithPropertiesSPI
			entity.fireValueRemovedChange(this, aggregateId)
			itemSet.remove(aggregateId)
			entity.doAfterRemove(this)
		}
	}

	// TODO too expensive?
	private fun isValidAggregateId(id: Any): Boolean = (repo as AggregateRepositorySPI<*>).persistenceProvider.isValidId(id)

}
