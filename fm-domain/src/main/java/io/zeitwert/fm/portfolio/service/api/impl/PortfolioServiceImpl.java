package io.zeitwert.fm.portfolio.service.api.impl;

import com.google.maps.GeoApiContext;
import com.google.maps.ImageResult;
import com.google.maps.StaticMapsApi;
import com.google.maps.StaticMapsRequest.ImageFormat;
import com.google.maps.StaticMapsRequest.Markers;
import com.google.maps.StaticMapsRequest.StaticMapType;
import com.google.maps.errors.ApiException;
import com.google.maps.model.Size;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.service.api.PortfolioService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("portfolioService")
public class PortfolioServiceImpl implements PortfolioService {

	private static final String GoogleApiKey = "AIzaSyBQF6Fi_Z0tZxVh5Eqzfx2m7hK3n718jsI";

	private final GeoApiContext context;

	@Autowired
	private ObjBuildingRepository buildingRepository;

	protected PortfolioServiceImpl() {
		this.context = new GeoApiContext.Builder()
				.apiKey(GoogleApiKey)
				.build();
	}

	@PreDestroy
	protected void destroy() {
		this.context.shutdown();
	}

	@Override
	public ImageResult getMap(ObjPortfolio portfolio, Size size) {
		try {
			Markers markers = new Markers();
			for (Object bldgId : portfolio.getBuildingSet()) {
				ObjBuilding bldg = this.buildingRepository.get(bldgId);
				String coordinates = bldg.getGeoCoordinates();
				if (coordinates != null && coordinates.startsWith("WGS:")) {
					markers.addLocation(coordinates.substring(4));
				}
			}
			return StaticMapsApi
					.newRequest(this.context, size)
					.markers(markers)
					// .center(address)
					// .zoom(zoom)
					.format(ImageFormat.jpg)
					.maptype(StaticMapType.roadmap)
					.await();
		} catch (ApiException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
