package io.zeitwert.fm.building.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.dddrive.obj.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "building", resourcePath = "building/buildings")
class ObjBuildingDto : ObjDtoBase()
