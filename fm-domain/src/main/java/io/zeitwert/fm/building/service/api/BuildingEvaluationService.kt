package io.zeitwert.fm.building.service.api

import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.service.api.dto.BuildingEvaluationResult

interface BuildingEvaluationService {

	/**
	 * Get the accumulated cost projection for a given building
	 *
	 * @param building the building
	 * @return building evaluation
	 */
	fun getEvaluation(building: ObjBuilding): BuildingEvaluationResult

}
