package io.dddrive.core.property.model

import io.dddrive.core.ddd.model.Part

interface EntityWithProperties {

	val isFrozen: Boolean

	fun hasProperty(name: String): Boolean

	fun getProperty(name: String): Property<*>

	val properties: List<Property<*>>

	fun hasPart(partId: Int): Boolean

	fun getPart(partId: Int): Part<*>

	fun <T> setValueByPath(
		relativePath: String,
		value: T?,
	)

}
