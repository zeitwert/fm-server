package io.zeitwert.dddrive.ddd.api.rest.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.enums.model.Enumerated
import io.zeitwert.dddrive.ddd.api.rest.impl.EnumeratedDeserializer

@JsonDeserialize(using = EnumeratedDeserializer::class)
data class EnumeratedDto(
	val id: String? = null,
	val name: String? = null,
) {

	override fun toString(): String = name ?: ""

	companion object {

		@JvmStatic
		fun of(
			id: String?,
			name: String?,
		): EnumeratedDto = EnumeratedDto(id, name)

		@JvmStatic
		fun of(e: Enumerated?): EnumeratedDto? = e?.let { EnumeratedDto(it.id, it.defaultName) }

		@JvmStatic
		fun of(a: Aggregate?): EnumeratedDto? = a?.let { EnumeratedDto(it.id.toString(), it.caption) }

		@JvmStatic
		fun of(
			p: Part<*>?,
			name: String?,
		): EnumeratedDto? = p?.let { EnumeratedDto(it.id.toString(), name) }
	}
}
