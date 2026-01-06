package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto

import dddrive.app.ddd.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.enums.model.Enumerated

data class EnumeratedDto(
	val id: String,
	val name: String? = null,
) {

	override fun toString(): String = "[$id: ${name ?: "__"}]"

	companion object {

		@JvmStatic
		fun of(
			id: String,
			name: String,
		): EnumeratedDto = EnumeratedDto(id, name)

		@JvmStatic
		fun of(e: Enumerated?): EnumeratedDto? = e?.let { EnumeratedDto(it.id, it.defaultName) }

		@JvmStatic
		fun of(a: Aggregate?): EnumeratedDto? = a?.let { EnumeratedDto(DtoUtils.idToString(it.id)!!, it.caption) }

		@JvmStatic
		fun of(
			p: Part<*>?,
			name: String?,
		): EnumeratedDto? = p?.let { EnumeratedDto(it.id.toString(), name) }
	}

}
