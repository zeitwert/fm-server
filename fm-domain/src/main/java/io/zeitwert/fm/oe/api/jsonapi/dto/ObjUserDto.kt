package io.zeitwert.fm.oe.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.dddrive.obj.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "user", resourcePath = "oe/users")
class ObjUserDto : ObjDtoBase()
