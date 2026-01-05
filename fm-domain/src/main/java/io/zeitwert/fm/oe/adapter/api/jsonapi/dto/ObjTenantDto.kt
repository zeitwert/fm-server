package io.zeitwert.fm.oe.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase
import io.zeitwert.fm.oe.model.ObjTenant

@JsonApiResource(type = "tenant", resourcePath = "oe/tenants")
class ObjTenantDto : ObjDtoBase<ObjTenant>() {

	@JsonApiRelationId
	var logoId: String? = null
		get() = getRelation("logoId") as String?
		set(value) {
			setRelation("logoId", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var logo: ObjDocumentDto? = null

}
