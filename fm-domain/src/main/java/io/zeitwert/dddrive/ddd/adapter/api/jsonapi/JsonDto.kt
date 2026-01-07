package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

interface JsonDto {

	val id: String? get() = this["id"] as? String

	fun containsKey(key: String): Boolean

	operator fun set(
		key: String,
		value: Any?,
	)

	operator fun get(key: String): Any?

}
