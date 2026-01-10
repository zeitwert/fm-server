package io.zeitwert.app.api.jsonapi.base

import io.crnk.core.resource.meta.MetaInformation
import io.zeitwert.app.api.jsonapi.AttributeDto

class PartInDto(
	val map: Map<String, Any?>,
) : Map<String, Any?> by map,
	AttributeDto {

	override val id: String? get() = getAttribute("id") as? String

	override fun hasAttribute(key: String): Boolean = map.containsKey(key)

	override fun getAttribute(key: String): Any? = map[key]

	override fun setAttribute(
		key: String,
		value: Any?,
	) = TODO()

}

class PartOutDto(
	private val map: MutableMap<String, Any?> = mutableMapOf(),
) : MutableMap<String, Any?> by map,
	AttributeDto {

	override val id: String? get() = getAttribute("id") as? String

	override fun hasAttribute(key: String): Boolean = map.containsKey(key)

	override fun setAttribute(
		key: String,
		value: Any?,
	) {
		map[key] = value
	}

	override fun getAttribute(key: String): Any? = map[key]

}

class MetaInfoOutDto(
	private val map: MutableMap<String, Any?> = mutableMapOf(),
) : MutableMap<String, Any?> by map,
	AttributeDto,
	MetaInformation {

	override val id: String? get() = getAttribute("id") as? String

	override fun hasAttribute(key: String): Boolean = map.containsKey(key)

	override fun setAttribute(
		key: String,
		value: Any?,
	) {
		map[key] = value
	}

	override fun getAttribute(key: String): Any? = map[key]

}
