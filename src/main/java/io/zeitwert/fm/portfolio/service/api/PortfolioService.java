package io.zeitwert.fm.portfolio.service.api;

import com.google.maps.ImageResult;
import com.google.maps.model.Size;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;

public interface PortfolioService {

	ImageResult getMap(ObjPortfolio portfolio, Size size);

}
