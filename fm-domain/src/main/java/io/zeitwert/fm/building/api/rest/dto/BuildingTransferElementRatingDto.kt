package io.zeitwert.fm.building.api.rest.dto

data class BuildingTransferElementRatingDto(
	var buildingPart: String,
	var weight: Int? = null,
	var condition: Int? = null,
	var ratingYear: Int? = null,
	var strain: Int? = null,
	var strength: Int? = null,
	var description: String? = null,
	var conditionDescription: String? = null,
	var measureDescription: String? = null,
)
