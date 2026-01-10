package dddrive.ddd.property.model

import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Enumeration

/**
 * Property that holds a set of enum values.
 *
 * Implements [Collection] interface so it can be used directly as a set in consumer code.
 */
interface EnumSetProperty<E : Enumerated> :
	Property<E>,
	Collection<E> {

	val enumeration: Enumeration<E>

	fun has(item: E): Boolean

	fun clear()

	fun add(item: E)

	fun remove(item: E)

}
