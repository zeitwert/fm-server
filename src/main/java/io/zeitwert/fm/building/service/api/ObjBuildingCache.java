package io.zeitwert.fm.building.service.api;

import io.dddrive.ddd.service.api.AggregateCache;
import io.zeitwert.fm.building.model.ObjBuilding;

import java.util.Map;

public interface ObjBuildingCache extends AggregateCache<ObjBuilding> {

	Map<String, Integer> getStatistics();

}
