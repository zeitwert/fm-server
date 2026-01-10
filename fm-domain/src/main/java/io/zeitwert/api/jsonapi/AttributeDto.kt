package io.zeitwert.api.jsonapi

interface AttributeDto {

	val id: String?

	fun hasAttribute(key: String): Boolean

	fun setAttribute(
		key: String,
		value: Any?,
	)

	fun getAttribute(key: String): Any?

}
