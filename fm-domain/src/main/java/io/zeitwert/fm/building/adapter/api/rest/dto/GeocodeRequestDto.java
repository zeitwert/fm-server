package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Data;

@Data
public class GeocodeRequestDto {

	private String street;
	private String zip;
	private String city;
	private String country;

	private String geoAddress;

}
