package io.zeitwert.app.api.jsonapi

import dddrive.app.ddd.model.Aggregate
import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Part
import io.zeitwert.app.api.jsonapi.dto.DtoUtils
import io.zeitwert.app.api.jsonapi.dto.SimpleEnumeratedDto
import io.zeitwert.app.api.jsonapi.dto.TypedEnumeratedDto

interface EnumeratedDto {

	val id: String

	val name: String?

	companion object {

		@JvmStatic
		fun of(
			id: String,
			name: String,
		): EnumeratedDto = SimpleEnumeratedDto(id, name)

		@JvmStatic
		fun of(e: Enumerated?): EnumeratedDto? = e?.let { SimpleEnumeratedDto(it.id, it.defaultName) }

		@JvmStatic
		fun of(a: Aggregate?): TypedEnumeratedDto? =
			when (a) {
				is Obj -> of(a)
				is Doc -> of(a)
				else -> null
			}

		@JvmStatic
		fun of(a: Obj?): TypedEnumeratedDto? =
			a?.let {
				val objType = of(it.meta.objTypeId, "")
				TypedEnumeratedDto(DtoUtils.idToString(it.id)!!, it.caption, objType)
			}

		@JvmStatic
		fun of(a: Doc?): TypedEnumeratedDto? =
			a?.let {
				val docType = of(it.meta.docTypeId, "")
				TypedEnumeratedDto(DtoUtils.idToString(it.id)!!, it.caption, docType)
			}

		@JvmStatic
		fun of(
			p: Part<*>?,
			name: String?,
		): EnumeratedDto? = p?.let { SimpleEnumeratedDto(it.id.toString(), name) }
	}

}
