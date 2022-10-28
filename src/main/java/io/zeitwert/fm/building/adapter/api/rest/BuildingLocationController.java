
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.maps.ImageResult;
import com.google.maps.model.Size;

import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.building.adapter.api.rest.dto.GeocodeRequestDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.GeocodeResponseDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.BuildingService;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;

@RestController("buildingLocationController")
@RequestMapping("/rest/building/buildings")
public class BuildingLocationController {

	private static final Integer DefaultGeoZoom = 17;
	private static final Size DefaultMapSize = new Size(800, 600);

	@Autowired
	private ObjBuildingRepository repo;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	BuildingService buildingService;

	@GetMapping("/{id}/location")
	protected ResponseEntity<byte[]> getMap(@PathVariable("id") Integer id) {

		ObjBuilding building = this.repo.get(id);

		String coordinates = building.getGeoCoordinates();
		Integer zoom = building.getGeoZoom() != null ? building.getGeoZoom() : DefaultGeoZoom;
		if (coordinates == null || !coordinates.startsWith("WGS:")) {
			return ResponseEntity.noContent().build();
		}

		ImageResult ir = buildingService.getMap(building.getName(), coordinates.substring(4), DefaultMapSize, zoom);
		if (ir == null) {
			return ResponseEntity.noContent().build();
		}

		ResponseEntity<byte[]> response = ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
				.body(ir.imageData);

		return response;
	}

	@PostMapping("/location")
	public ResponseEntity<GeocodeResponseDto> getAddress(@RequestBody GeocodeRequestDto request) {
		if (request.getGeoAddress() != null && !request.getGeoAddress().equals("")) {
		} else if (request.getCountry() == null) {
			return ResponseEntity.badRequest().build();
		} else if (request.getStreet() == null || request.getZip() == null || request.getCity() == null) {
			return ResponseEntity.badRequest().build();
		}
		String address = request.getGeoAddress();
		if (address == null || address.equals("")) {
			address = request.getStreet() + ", " + request.getZip() + " " + request.getCity() + ", " + request.getCountry();
		}
		String coordinates = this.buildingService.getCoordinates(address);
		if (coordinates == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok()
				.body(GeocodeResponseDto.builder().geoCoordinates(coordinates).geoZoom(DefaultGeoZoom).build());
	}

	@RequestMapping(value = "/{id}/coverFoto", method = RequestMethod.POST)
	public ResponseEntity<Void> storeCoverFoto(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
		try {
			ObjBuilding building = this.repo.get(id);
			ObjDocument document = building.getCoverFoto();
			CodeContentType contentType = CodeContentTypeEnum.getContentType(file.getContentType(),
					file.getOriginalFilename());
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null);
			}
			document.storeContent(contentType, file.getBytes());
			building.calcAll();
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok().body(null);
	}

}
