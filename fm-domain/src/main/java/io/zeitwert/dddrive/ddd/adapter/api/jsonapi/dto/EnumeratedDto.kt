package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.Part
import dddrive.ddd.enums.model.Enumerated
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.DtoUtils

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
		fun of(a: Obj?): EnumeratedDto? = a?.let { EnumeratedDto(DtoUtils.idToString(it.id), it.caption) }

		@JvmStatic
		fun of(a: Doc?): EnumeratedDto? = a?.let { EnumeratedDto(DtoUtils.idToString(it.id), it.caption) }

		@JvmStatic
		fun of(
			p: Part<*>?,
			name: String?,
		): EnumeratedDto? = p?.let { EnumeratedDto(it.id.toString(), name) }
	}

}
