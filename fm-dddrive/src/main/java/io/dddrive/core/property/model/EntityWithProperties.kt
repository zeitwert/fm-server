package io.dddrive.core.property.model

import io.dddrive.core.ddd.model.Part
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
