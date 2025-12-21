package io.zeitwert.fm.building.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto

data class BuildingPartWeightDto(
    var part: EnumeratedDto? = null,
    var weight: Int? = null,
    var lifeTime20: Int? = null,
    var lifeTime50: Int? = null,
    var lifeTime70: Int? = null,
    var lifeTime85: Int? = null,
    var lifeTime95: Int? = null,
    var lifeTime100: Int? = null,
)



