package io.zeitwert.fm.building.service.api

import com.google.maps.ImageResult
import com.google.maps.model.GeocodingResult
import com.google.maps.model.Size

interface BuildingService {

	fun getCoordinates(address: String): String?

	fun getGeocoding(address: String): GeocodingResult?

	fun getMap(address: String, size: Size, zoom: Int): ImageResult?

}
