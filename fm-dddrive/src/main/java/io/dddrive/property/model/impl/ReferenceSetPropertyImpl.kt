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

	private val items: MutableSet<Any> = mutableSetOf()

	override fun clear() {
		require(isWritable) { "writable" }
		items.toSet().forEach { remove(it) }
		check(items.isEmpty()) { "all items removed" }
		items.clear()
		(entity as EntityWithPropertiesSPI).doAfterClear(this)
	}

	override fun add(aggregateId: Any) {
		require(isWritable) { "writable" }
		require(isValidAggregateId(aggregateId)) { "valid aggregate id [$aggregateId]" }
		if (has(aggregateId)) {
			return
		}
		if (!has(aggregateId)) {
			val entity = entity as EntityWithPropertiesSPI
			entity.fireValueAddedChange(this, aggregateId)
			items.add(aggregateId)
			entity.doAfterAdd(this, null)
		}
	}

	override fun has(aggregateId: Any): Boolean = items.contains(aggregateId)

	override fun remove(aggregateId: Any) {
		require(isWritable) { "writable" }
		require(isValidAggregateId(aggregateId)) { "valid aggregate id [$aggregateId]" }
		if (has(aggregateId)) {
			val entity = entity as EntityWithPropertiesSPI
			entity.fireValueRemovedChange(this, aggregateId)
			items.remove(aggregateId)
			entity.doAfterRemove(this)
		}
	}

	// TODO too expensive?
	private fun isValidAggregateId(id: Any): Boolean = (repo as AggregateRepositorySPI<*>).persistenceProvider.isValidId(id)

	override val size: Int get() = items.size

	override fun isEmpty(): Boolean = items.isEmpty()

	override fun iterator(): Iterator<Any> = items.iterator()

	override fun containsAll(elements: Collection<Any>): Boolean = items.containsAll(elements)

	override fun contains(element: Any): Boolean = items.contains(element)

}
