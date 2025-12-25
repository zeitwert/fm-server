package io.dddrive.property.model

import io.dddrive.ddd.model.Part

/**
 * Property that holds a list of parts.
 *
 * Implements [Iterable] and [Collection] interfaces so it can be used directly as a list in consumer code.
 */
interface PartListProperty<P : Part<*>> :
	Property<P>,
	Iterable<P>,
	Collection<P> {

	val partType: Class<P>

	override val size: Int

	operator fun get(seqNr: Int): P

	fun getById(partId: Int): P

	/**
	 * Removes all parts from this list.
	 * Alias for [clear].
	 */
	fun clear()

	fun add(partId: Int? = null): P

	fun remove(partId: Int)

	fun remove(part: P)

	/**
	 * Returns the index of the specified part within this list.
	 *
	 * @param part the part to find
	 * @return the index of the part, or -1 if the part is not in this list.
	 */
	fun indexOf(part: P): Int

}
