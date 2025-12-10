package io.zeitwert.fm.building.service.api;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult;

public interface BuildingEvaluationService {

	/**
	 * Get the accumulated cost projection for a given building
	 * 
	 * @param building the building
	 * @return building evaluation
	 */
	BuildingEvaluationResult getEvaluation(ObjBuilding building);

}
