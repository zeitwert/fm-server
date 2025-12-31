package dddrive.ddd.property.model

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part

/**
 * Property that holds a list of parts.
 *
 * Implements [Collection] interface so it can be used directly as a list in consumer code.
 */
interface PartListProperty<A : Aggregate, P : Part<A>> :
	Property<P>,
	Collection<P> {

	val partType: Class<P>

	override val size: Int

	operator fun get(seqNr: Int): P

	fun getById(partId: Int): P

	fun clear()

	fun add(partId: Int? = null): P

	fun remove(partId: Int)

	fun remove(part: P)

	fun indexOf(part: P): Int

}
