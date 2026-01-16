package io.zeitwert.app.api.jsonapi

import dddrive.app.ddd.model.Aggregate
import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.ddd.model.Enumerated
import dddrive.ddd.model.Part
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.zeitwert.app.api.jsonapi.dto.DtoUtils
import io.zeitwert.app.api.jsonapi.dto.SimpleEnumeratedDto
import io.zeitwert.app.api.jsonapi.dto.TypedEnumeratedDto
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjUser

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
		fun of(a: Aggregate?): EnumeratedDto? =
			when (a) {
				is Obj -> of(a)
				is Doc -> of(a)
				else -> null
			}

		@JvmStatic
		fun of(a: Obj?): EnumeratedDto? =
			a?.let {
				val objType = CodeAggregateTypeEnum.getAggregateType(it.meta.objTypeId)
				when {
					a is ObjTenant -> SimpleEnumeratedDto(DtoUtils.idToString(it.id)!!, it.caption)
					a is ObjUser -> SimpleEnumeratedDto(DtoUtils.idToString(it.id)!!, it.caption)
					else -> TypedEnumeratedDto(DtoUtils.idToString(it.id)!!, it.caption, of(objType)!!)
				}
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
