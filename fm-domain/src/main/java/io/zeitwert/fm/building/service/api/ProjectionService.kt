package io.zeitwert.fm.building.service.api

import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.service.api.dto.ProjectionResult

interface ProjectionService {

	/**
	 * Get the accumulated cost projection for a given set of buildings
	 *
	 * @param buildings the buildings (1 .. n)
	 * @return cost projection
	 */
	fun getProjection(buildings: Set<ObjBuilding>, duration: Int): ProjectionResult

	companion object {

		const val DefaultDuration: Int = 25
	}

}
