package dddrive.ddd.property.model

import dddrive.ddd.core.model.Part
import kotlin.reflect.KClass

interface EntityWithProperties {

	val isFrozen: Boolean

	fun hasProperty(name: String): Boolean

	fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T>

	val properties: List<Property<*>>

	fun hasPart(partId: Int): Boolean

	fun getPart(partId: Int): Part<*>

}
