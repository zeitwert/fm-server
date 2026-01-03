package io.zeitwert.fm.oe.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.GenericObjDtoBase
import io.zeitwert.fm.oe.model.ObjUser

@JsonApiResource(type = "user", resourcePath = "oe/users")
class ObjUserDto : GenericObjDtoBase<ObjUser>() {

	@JsonApiRelationId
	var avatarId: String? = null
		get() = getRelation("avatarId") as String?
		set(value) {
			setRelation("avatarId", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var avatar: ObjDocumentDto? = null

}
