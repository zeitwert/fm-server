package io.zeitwert.fm.building.adapter.rest

import com.google.maps.model.Size
import io.zeitwert.fm.building.adapter.rest.dto.GeocodeRequestDto
import io.zeitwert.fm.building.adapter.rest.dto.GeocodeResponseDto
import io.zeitwert.fm.building.api.BuildingService
import io.zeitwert.fm.building.model.ObjBuildingRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("buildingLocationController")
@RequestMapping("/rest/building/buildings")
class BuildingLocationController(
	val buildingRepo: ObjBuildingRepository,
	val buildingService: BuildingService,
) {

	@GetMapping("/{id}/location")
	protected fun getMap(
		@PathVariable("id") id: Int,
	): ResponseEntity<ByteArray?> {
		val building = this.buildingRepo.get(id)

		val coordinates = building.geoCoordinates
		val zoom: Int = (if (building.geoZoom != null) building.geoZoom else DefaultGeoZoom)!!
		if (coordinates == null || !coordinates.startsWith("WGS:")) {
			return ResponseEntity.noContent().build<ByteArray?>()
		}

		val ir = this.buildingService.getMap(coordinates.substring(4), DefaultMapSize, zoom)
		if (ir == null) {
			return ResponseEntity.noContent().build<ByteArray?>()
		}

		return ResponseEntity
			.ok()
			.contentType(MediaType.IMAGE_JPEG)
			.body<ByteArray?>(ir.imageData)
	}

	@PostMapping("/location")
	fun getAddress(
		@RequestBody request: GeocodeRequestDto,
	): ResponseEntity<GeocodeResponseDto?> {
		if (request.geoAddress != null && "" != request.geoAddress) {
		} else if (request.country == null) {
			return ResponseEntity.badRequest().build<GeocodeResponseDto?>()
		} else if (request.street == null || request.zip == null || request.city == null) {
			return ResponseEntity.badRequest().build<GeocodeResponseDto?>()
		}
		var address = request.geoAddress
		if (address == null || "" == address) {
			address = request.street + ", " + request.zip + " " + request.city + ", " + request.country
		}
		val coordinates = this.buildingService.getCoordinates(address)
		if (coordinates == null) {
			return ResponseEntity.notFound().build<GeocodeResponseDto?>()
		}
		return ResponseEntity
			.ok()
			.body<GeocodeResponseDto?>(
				GeocodeResponseDto(geoCoordinates = coordinates, geoZoom = DefaultGeoZoom),
			)
	}

	companion object {

		private const val DefaultGeoZoom = 17
		private val DefaultMapSize = Size(800, 600)
	}

}
