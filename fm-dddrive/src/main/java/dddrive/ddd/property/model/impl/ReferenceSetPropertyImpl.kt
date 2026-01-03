package dddrive.ddd.property.model.impl

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregateRepository
import dddrive.ddd.core.model.AggregateRepositorySPI
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.ReferenceSetProperty
import dddrive.ddd.property.model.base.PropertyBase

class ReferenceSetPropertyImpl<A : Aggregate>(
	entity: EntityWithProperties,
	name: String,
	val repo: AggregateRepository<A>,
	override val targetClass: Class<A>,
) : PropertyBase<A>(entity, name),
	ReferenceSetProperty<A> {

	private val items: MutableSet<Any> = mutableSetOf()

	override fun clear() {
		require(isWritable) { "writable" }
		items.toList().forEach { remove(it) }
		check(items.isEmpty()) { "all items removed" }
		check(items.isEmpty()) { "items empty" }
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
			fireValueAddedChange(aggregateId)
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
			fireValueRemovedChange(aggregateId)
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
