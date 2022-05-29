package io.zeitwert.fm.building.service.api;

import com.google.maps.ImageResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Size;

public interface BuildingService {

	String getCoordinates(String address);

	GeocodingResult getGeocoding(String address);

	ImageResult getMap(String name, String address, Size size, int zoom);

}
