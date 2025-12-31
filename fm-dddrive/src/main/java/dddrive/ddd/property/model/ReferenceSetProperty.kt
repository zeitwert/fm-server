package dddrive.ddd.property.model

import dddrive.ddd.core.model.Aggregate

/**
 * Property that holds a set of aggregate references (by ID).
 *
 * Implements [Collection] interface so it can be used directly as a set in consumer code.
 * Note: The set contains IDs, not the aggregates themselves.
 */
interface ReferenceSetProperty<A : Aggregate> :
	Property<A>,
	Collection<Any> {

	fun has(aggregateId: Any): Boolean

	fun clear()

	fun add(aggregateId: Any)

	fun remove(aggregateId: Any)

}
