package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

interface GenericDto {

	fun hasAttribute(name: String): Boolean

	operator fun set(
		name: String,
		value: Any?,
	)

	operator fun get(name: String): Any?

}
