package io.zeitwert.fm.building.api.rest.dto

data class GeocodeRequestDto(
	var street: String? = null,
	var zip: String? = null,
	var city: String? = null,
	var country: String? = null,
	var geoAddress: String? = null,
)
