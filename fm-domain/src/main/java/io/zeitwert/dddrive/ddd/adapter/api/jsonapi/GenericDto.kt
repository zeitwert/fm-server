package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto

interface GenericDto {

	val id: String? get() = this["id"] as? String

	fun hasAttribute(name: String): Boolean

	operator fun set(
		name: String,
		value: Any?,
	)

	operator fun get(name: String): Any?

	// Get the ID of an enumerated field
	// From the UI we only get Maps, but the original value contains EnumeratedDto
	// crnk always fetches the original value first when deserializing, so we need to handle both cases
	fun enumId(fieldName: String): String? = enumId(this[fieldName])

	@Suppress("UNCHECKED_CAST")
	fun enumId(field: Any?): String? =
		when (field) {
			null -> null
			is EnumeratedDto -> field.id
			is Map<*, *> -> (field as Map<String, Any?>)["id"] as? String
			else -> null
		}

}
