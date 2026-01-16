package io.zeitwert.app.api.jsonapi.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto

data class SimpleEnumeratedDto(
	override val id: String,
	override val name: String? = null,
) : EnumeratedDto {

	override fun toString(): String = "[$id: ${name ?: "__"}]"

}

data class TypedEnumeratedDto(
	override val id: String,
	override val name: String? = null,
	val itemType: EnumeratedDto,
) : EnumeratedDto {

	override fun toString(): String = "[$id: ${name ?: "__"} (${itemType.id.substring(4)})]"

}
