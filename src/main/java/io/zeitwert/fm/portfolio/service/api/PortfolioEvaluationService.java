package io.zeitwert.fm.portfolio.service.api;

import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.service.api.dto.PortfolioEvaluationResult;

public interface PortfolioEvaluationService {

	/**
	 * Get the accumulated cost projection for a given portfolio
	 * 
	 * @param portfolio the portfolio
	 * @return portfolio evaluation
	 */
	PortfolioEvaluationResult getEvaluation(ObjPortfolio portfolio);

}
