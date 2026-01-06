package io.zeitwert.fm.building.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "building", resourcePath = "building/buildings")
class ObjBuildingDto : ObjDtoBase<ObjBuilding>()
