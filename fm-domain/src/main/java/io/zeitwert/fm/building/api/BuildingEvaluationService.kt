package io.zeitwert.fm.building.api

import io.zeitwert.fm.building.api.dto.BuildingEvaluationResult
import io.zeitwert.fm.building.model.ObjBuilding

interface BuildingEvaluationService {

	/**
	 * Get the accumulated cost projection for a given building
	 *
	 * @param building the building
	 * @return building evaluation
	 */
	fun getEvaluation(building: ObjBuilding): BuildingEvaluationResult

}
