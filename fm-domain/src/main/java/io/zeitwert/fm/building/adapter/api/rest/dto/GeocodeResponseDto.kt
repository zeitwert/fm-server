package io.zeitwert.fm.building.adapter.api.rest.dto

data class GeocodeResponseDto(
    var geoCoordinates: String? = null,
    var geoZoom: Int? = null,
)

