package io.zeitwert.fm.building.service.api;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.service.api.dto.ProjectionResult;

import java.util.Set;

public interface ProjectionService {

	static final int DefaultDuration = 25;

	/**
	 * Get the accumulated cost projection for a given set of buildings
	 * 
	 * @param buildings the buildings (1 .. n)
	 * @return cost projection
	 */
	ProjectionResult getProjection(Set<ObjBuilding> buildings, int duration);

}
