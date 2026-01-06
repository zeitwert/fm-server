package io.zeitwert.fm.oe.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase
import io.zeitwert.fm.oe.model.ObjTenant

@JsonApiResource(type = "tenant", resourcePath = "oe/tenants")
class ObjTenantDto : ObjDtoBase<ObjTenant>()
