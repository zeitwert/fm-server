package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeocodeResponseDto {

	private String geoCoordinates;
	private Integer geoZoom;

}
