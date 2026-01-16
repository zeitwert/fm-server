package io.zeitwert.fm.building.adapter.rest.dto

import io.zeitwert.app.api.jsonapi.EnumeratedDto

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
