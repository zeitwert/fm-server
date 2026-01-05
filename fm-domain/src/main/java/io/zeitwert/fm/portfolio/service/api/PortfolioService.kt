package io.zeitwert.fm.portfolio.service.api

import com.google.maps.ImageResult
import com.google.maps.model.Size
import io.zeitwert.fm.portfolio.model.ObjPortfolio

interface PortfolioService {

	fun getMap(
		portfolio: ObjPortfolio,
		size: Size,
	): ImageResult?

}
