package io.dddrive.core.property.model

interface BaseProperty<T : Any> : Property<T> {

	val type: Class<T>

	var value: T?

}
