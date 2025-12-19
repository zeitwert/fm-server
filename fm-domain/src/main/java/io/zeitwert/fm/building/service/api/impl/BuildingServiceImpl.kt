package io.zeitwert.fm.building.service.api.impl

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.ImageResult
import com.google.maps.StaticMapsApi
import com.google.maps.StaticMapsRequest.ImageFormat
import com.google.maps.StaticMapsRequest.Markers
import com.google.maps.StaticMapsRequest.StaticMapType
import com.google.maps.errors.ApiException
import com.google.maps.model.GeocodingResult
import com.google.maps.model.Size
import io.zeitwert.fm.building.service.api.BuildingService
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service("buildingService")
class BuildingServiceImpl() : BuildingService {

	private val context: GeoApiContext

	init {
		this.context = GeoApiContext.Builder()
			.apiKey(GoogleApiKey)
			.build()
	}

	@PreDestroy
	fun destroy() {
		context.shutdown()
	}

	override fun getCoordinates(address: String): String? {
		// check if address is already a coordinate
		val matcher: Matcher = CoordinatePattern.matcher(address)
		if (matcher.find()) {
			val x = matcher.group(1).toDouble()
			val y = matcher.group(2).toDouble()
			if (x < -90 || 90 < x) {
				return null
			} else if (y < -180 || 180 < y) {
				return null
			}
			return "WGS:" + address
		}
		val result = this.getGeocoding(address)
		if (result == null) {
			return null
		}
		val lat = String.format("%.7g", result.geometry.location.lat)
		val lng = String.format("%.7g", result.geometry.location.lng)
		return "WGS:" + lat + "," + lng
	}

	/*
	 * // Convert WGS lat/long (° dec) to CH x
	 * private double WGStoCHx(double lat, double lng) {
	 * // Converts dec degrees to sex seconds
	 * lat = DecToSexAngle(lat);
	 * lng = DecToSexAngle(lng);
	 *
	 * // Auxiliary values (% Bern)
	 * double lat_aux = (lat - 169028.66) / 10000;
	 * double lng_aux = (lng - 26782.5) / 10000;
	 *
	 * // Process X
	 * double x = ((200147.07 + (308807.95 * lat_aux)
	 * + (3745.25 * Math.pow(lng_aux, 2)) + (76.63 * Math.pow(lat_aux, 2)))
	 * - (194.56 * Math.pow(lng_aux, 2) * lat_aux))
	 * + (119.79 * Math.pow(lat_aux, 3));
	 *
	 * return x;
	 * }
	 *
	 * // Convert WGS lat/long (° dec) to CH y
	 * private double WGStoCHy(double lat, double lng) {
	 * // Converts dec degrees to sex seconds
	 * lat = DecToSexAngle(lat);
	 * lng = DecToSexAngle(lng);
	 *
	 * // Auxiliary values (% Bern)
	 * double lat_aux = (lat - 169028.66) / 10000;
	 * double lng_aux = (lng - 26782.5) / 10000;
	 *
	 * // Process Y
	 * double y = (600072.37 + (211455.93 * lng_aux))
	 * - (10938.51 * lng_aux * lat_aux)
	 * - (0.36 * lng_aux * Math.pow(lat_aux, 2))
	 * - (44.54 * Math.pow(lng_aux, 3));
	 *
	 * return y;
	 * }
	 *
	 * // Convert decimal angle (degrees) to sexagesimal angle (seconds)
	 * private double DecToSexAngle(double dec) {
	 * int deg = (int) Math.floor(dec);
	 * int min = (int) Math.floor((dec - deg) * 60);
	 * double sec = (((dec - deg) * 60) - min) * 60;
	 *
	 * return sec + min * 60.0 + deg * 3600.0;
	 * }
	 */
	override fun getGeocoding(address: String): GeocodingResult? {
		try {
			val results = GeocodingApi.geocode(context, address).await()
			if (results.size != 1) {
				return null
			} else if (results[0]!!.partialMatch) {
				return null
			}
			return results[0]
		} catch (e: ApiException) {
			e.printStackTrace()
		} catch (e: InterruptedException) {
			e.printStackTrace()
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return null
	}

	override fun getMap(address: String, size: Size, zoom: Int): ImageResult? {
		try {
			val markers = Markers()
			markers.addLocation(address)
			return StaticMapsApi.newRequest(context, size).markers(markers).center(address).zoom(zoom).format(ImageFormat.jpg)
				.maptype(StaticMapType.roadmap).await()
		} catch (e: ApiException) {
			e.printStackTrace()
		} catch (e: InterruptedException) {
			e.printStackTrace()
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return null
	}

	companion object {

		private const val GoogleApiKey = "AIzaSyBQF6Fi_Z0tZxVh5Eqzfx2m7hK3n718jsI"

		private val CoordinatePattern: Pattern = Pattern.compile("(-?[\\d]*\\.[\\d]*),[\\s]*(-?[\\d]*\\.[\\d]*)")
	}

}
