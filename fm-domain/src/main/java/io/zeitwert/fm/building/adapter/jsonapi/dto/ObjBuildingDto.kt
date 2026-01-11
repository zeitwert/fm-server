package io.zeitwert.fm.building.adapter.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "building", resourcePath = "building/buildings")
class ObjBuildingDto : ObjDtoBase()
