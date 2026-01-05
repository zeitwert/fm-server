package io.zeitwert.fm.portfolio.service.api.impl

import com.google.maps.GeoApiContext
import com.google.maps.ImageResult
import com.google.maps.StaticMapsApi
import com.google.maps.StaticMapsRequest.ImageFormat
import com.google.maps.StaticMapsRequest.Markers
import com.google.maps.StaticMapsRequest.StaticMapType
import com.google.maps.errors.ApiException
import com.google.maps.model.Size
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.service.api.PortfolioService
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service("portfolioService")
class PortfolioServiceImpl : PortfolioService {

	private val context: GeoApiContext

	@Autowired
	lateinit var buildingRepository: ObjBuildingRepository

	init {
		this.context = GeoApiContext
			.Builder()
			.apiKey(GoogleApiKey)
			.build()
	}

	@PreDestroy
	fun destroy() {
		this.context.shutdown()
	}

	override fun getMap(
		portfolio: ObjPortfolio,
		size: Size,
	): ImageResult? {
		try {
			val markers = Markers()
			for (bldgId in portfolio.buildingSet) {
				val bldg = this.buildingRepository.get(bldgId)
				val coordinates = bldg.geoCoordinates
				if (coordinates != null && coordinates.startsWith("WGS:")) {
					markers.addLocation(coordinates.substring(4))
				}
			}
			return StaticMapsApi
				.newRequest(this.context, size)
				.markers(markers) // .center(address)
				// .zoom(zoom)
				.format(ImageFormat.jpg)
				.maptype(StaticMapType.roadmap)
				.await()
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
	}

}
