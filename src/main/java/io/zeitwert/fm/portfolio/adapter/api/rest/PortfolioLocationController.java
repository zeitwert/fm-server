
package io.zeitwert.fm.portfolio.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.ImageResult;
import com.google.maps.model.Size;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.service.api.ObjPortfolioCache;
import io.zeitwert.fm.portfolio.service.api.PortfolioService;

@RestController("portfolioLocationController")
@RequestMapping("/rest/portfolio/portfolios")
public class PortfolioLocationController {

	private static final Size DefaultMapSize = new Size(800, 600);

	@Autowired
	private ObjPortfolioCache cache;

	@Autowired
	PortfolioService portfolioService;

	@GetMapping("/{id}/location")
	protected ResponseEntity<byte[]> getMap(@PathVariable("id") Integer id) {
		ObjPortfolio portfolio = this.cache.get(id);
		ImageResult ir = this.portfolioService.getMap(portfolio, DefaultMapSize);
		if (ir == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity
				.ok()
				.contentType(MediaType.IMAGE_JPEG)
				.body(ir.imageData);
	}

}
