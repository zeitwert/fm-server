package io.dddrive.property.model

interface BaseProperty<T : Any> : Property<T> {

	val type: Class<T>

	var value: T?

}
