package io.zeitwert.fm.building.api

import io.zeitwert.fm.building.api.dto.ProjectionResult
import io.zeitwert.fm.building.model.ObjBuilding

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
