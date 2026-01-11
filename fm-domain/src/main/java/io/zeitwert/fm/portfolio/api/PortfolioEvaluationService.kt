package io.zeitwert.fm.portfolio.api

import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.api.dto.PortfolioEvaluationResult

interface PortfolioEvaluationService {

	/**
	 * Get the accumulated cost projection for a given portfolio
	 *
	 * @param portfolio the portfolio
	 * @return portfolio evaluation
	 */
	fun getEvaluation(portfolio: ObjPortfolio): PortfolioEvaluationResult

}
