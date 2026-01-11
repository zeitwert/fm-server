package dddrive.property.model

import kotlin.reflect.KClass

interface EntityWithProperties {

	val isFrozen: Boolean

	val isInLoad: Boolean

	val isInCalc: Boolean

	val properties: List<Property<*>>

	fun hasProperty(name: String): Boolean

	fun <T : Any> getProperty(
		name: String,
		type: KClass<T>,
	): Property<T>

	fun addProperty(property: Property<*>)

}
