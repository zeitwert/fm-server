package io.dddrive.property.model

import io.dddrive.enums.model.Enumerated

/**
 * Property that holds a set of enum values.
 *
 * Implements [Iterable] and [Collection] interfaces so it can be used directly as a set in consumer code.
 */
interface EnumSetProperty<E : Enumerated> :
	Property<E>,
	Collection<E> {

	fun has(item: E): Boolean

	fun clear()

	fun add(item: E)

	fun remove(item: E)

}
