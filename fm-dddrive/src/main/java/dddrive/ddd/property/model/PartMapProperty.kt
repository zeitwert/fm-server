package dddrive.ddd.property.model

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part

/**
 * Property that holds a string-indexed map of parts.
 *
 * Implements [Collection] and [Map] interfaces so it can be used directly as a map in consumer
 * code.
 */
interface PartMapProperty<A : Aggregate, P : Part<A>> :
	Property<P>,
	Collection<P>,
	Map<String, P> {

	val partType: Class<P>

	override val size: Int

	override operator fun get(key: String): P

	fun clear()

	fun add(
		key: String,
		partId: Int? = null,
	): P

	override fun containsKey(key: String): Boolean

	fun remove(key: String)

	fun remove(part: P)

	fun keyOf(part: Part<*>): String

}
