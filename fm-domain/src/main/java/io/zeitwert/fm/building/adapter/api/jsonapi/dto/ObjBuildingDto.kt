package io.zeitwert.fm.building.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.GenericObjDtoBase

@JsonApiResource(type = "building", resourcePath = "building/buildings")
class ObjBuildingDto : GenericObjDtoBase<ObjBuilding>() {

	@JsonApiRelationId
	var accountId: String? = null
		get() = getRelation("accountId") as String?
		set(value) {
			setRelation("accountId", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var account: ObjAccountDto? = null

	@JsonApiRelationId
	@Suppress("UNCHECKED_CAST")
	var contactIds: List<String>? = null
		get() = getRelation("contactIds") as List<String>?
		set(value) {
			setRelation("contactIds", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY, idField = "contactIds")
	var contacts: List<ObjContactDto>? = null

	@JsonApiRelationId
	var coverFotoId: String? = null
		get() = getRelation("coverFotoId") as String?
		set(value) {
			setRelation("coverFotoId", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var coverFoto: ObjDocumentDto? = null
}
