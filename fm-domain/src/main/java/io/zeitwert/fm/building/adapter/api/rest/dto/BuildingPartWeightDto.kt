package io.zeitwert.fm.building.adapter.api.rest.dto

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto

data class BuildingPartWeightDto(
	var part: EnumeratedDto,
	var weight: Int,
	var lifeTime20: Int,
	var lifeTime50: Int,
	var lifeTime70: Int,
	var lifeTime85: Int,
	var lifeTime95: Int,
	var lifeTime100: Int,
)
