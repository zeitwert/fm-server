package io.zeitwert.fm.portfolio.adapter.rest

import com.google.maps.model.Size
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.portfolio.api.PortfolioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("portfolioLocationController")
@RequestMapping("/rest/portfolio/portfolios")
class PortfolioLocationController {

	@Autowired
	lateinit var portfolioRepository: ObjPortfolioRepository

	@Autowired
	lateinit var portfolioService: PortfolioService

	@GetMapping("/{id}/location")
	fun getMap(
		@PathVariable("id") id: Int,
	): ResponseEntity<ByteArray> {
		val portfolio = this.portfolioRepository.get(id)
		val ir = this.portfolioService.getMap(portfolio, DefaultMapSize)
		if (ir == null) {
			return ResponseEntity.noContent().build()
		}
		return ResponseEntity
			.ok()
			.contentType(MediaType.IMAGE_JPEG)
			.body(ir.imageData)
	}

	companion object {

		private val DefaultMapSize = Size(800, 600)
	}

}
