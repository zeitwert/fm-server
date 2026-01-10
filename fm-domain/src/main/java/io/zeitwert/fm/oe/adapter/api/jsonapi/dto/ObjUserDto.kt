package io.zeitwert.fm.oe.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "user", resourcePath = "oe/users")
class ObjUserDto : ObjDtoBase()
