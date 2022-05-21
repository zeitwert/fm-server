package io.zeitwert.fm.building.service.api;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;
import io.zeitwert.fm.building.service.api.dto.PortfolioEvaluationResult;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;

public interface EvaluationService {

	/**
	 * Get the accumulated cost projection for a given building
	 * 
	 * @param building the building
	 * @return building evaluation
	 */
	BuildingEvaluationResult getEvaluation(ObjBuilding building);

	/**
	 * Get the accumulated cost projection for a given portfolio
	 * 
	 * @param portfolio the portfolio
	 * @return portfolio evaluation
	 */
	PortfolioEvaluationResult getEvaluation(ObjPortfolio portfolio);

}
