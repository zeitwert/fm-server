package dddrive.ddd.property.model

import dddrive.ddd.enums.model.Enumerated

/**
 * Property that holds a set of enum values.
 *
 * Implements [Collection] interface so it can be used directly as a set in consumer code.
 */
interface EnumSetProperty<E : Enumerated> :
	Property<E>,
	Collection<E> {

	fun has(item: E): Boolean

	fun clear()

	fun add(item: E)

	fun remove(item: E)

}
