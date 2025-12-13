
package io.zeitwert.fm.building.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.ImageResult;
import com.google.maps.model.Size;
import io.zeitwert.fm.building.adapter.api.rest.dto.GeocodeRequestDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.GeocodeResponseDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.BuildingService;

@RestController("buildingLocationController")
@RequestMapping("/rest/building/buildings")
public class BuildingLocationController {

	private static final Integer DefaultGeoZoom = 17;
	private static final Size DefaultMapSize = new Size(800, 600);

	@Autowired
	private ObjBuildingRepository cache;

	@Autowired
	BuildingService buildingService;

	@GetMapping("/{id}/location")
	protected ResponseEntity<byte[]> getMap(@PathVariable("id") Integer id) {

		ObjBuilding building = this.cache.get(id);

		String coordinates = building.getGeoCoordinates();
		Integer zoom = building.getGeoZoom() != null ? building.getGeoZoom() : DefaultGeoZoom;
		if (coordinates == null || !coordinates.startsWith("WGS:")) {
			return ResponseEntity.noContent().build();
		}

		ImageResult ir = this.buildingService.getMap(building.getName(), coordinates.substring(4), DefaultMapSize, zoom);
		if (ir == null) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity
				.ok()
				.contentType(MediaType.IMAGE_JPEG)
				.body(ir.imageData);

	}

	@PostMapping("/location")
	public ResponseEntity<GeocodeResponseDto> getAddress(@RequestBody GeocodeRequestDto request) {
		if (request.getGeoAddress() != null && !"".equals(request.getGeoAddress())) {
		} else if (request.getCountry() == null) {
			return ResponseEntity.badRequest().build();
		} else if (request.getStreet() == null || request.getZip() == null || request.getCity() == null) {
			return ResponseEntity.badRequest().build();
		}
		String address = request.getGeoAddress();
		if (address == null || "".equals(address)) {
			address = request.getStreet() + ", " + request.getZip() + " " + request.getCity() + ", " + request.getCountry();
		}
		String coordinates = this.buildingService.getCoordinates(address);
		if (coordinates == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok()
				.body(GeocodeResponseDto.builder().geoCoordinates(coordinates).geoZoom(DefaultGeoZoom).build());
	}

}
