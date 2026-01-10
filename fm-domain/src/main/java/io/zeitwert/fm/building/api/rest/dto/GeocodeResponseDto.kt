package io.zeitwert.fm.building.api.rest.dto

data class GeocodeResponseDto(
	var geoCoordinates: String? = null,
	var geoZoom: Int? = null,
)
