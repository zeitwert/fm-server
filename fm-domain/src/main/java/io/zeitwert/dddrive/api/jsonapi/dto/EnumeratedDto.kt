package io.zeitwert.dddrive.api.jsonapi.dto

import dddrive.app.ddd.model.Aggregate
import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Part

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
		): EnumeratedDto? = p?.let { EnumeratedDto(it.id.toString(), name) }
	}

}

data class TypedEnumeratedDto(
	val id: String,
	val name: String? = null,
	val itemType: EnumeratedDto,
) {

	override fun toString(): String = "[$id: ${name ?: "__"} (${itemType.id.substring(4)})]"

}
