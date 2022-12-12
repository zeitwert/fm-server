package io.zeitwert.fm.building.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.service.api.ObjBuildingCache;

@Service("buildingCache")
public class ObjBuildingCacheImpl extends AggregateCacheBase<ObjBuilding> implements ObjBuildingCache {

	public ObjBuildingCacheImpl(ObjBuildingRepository repository) {
		super(repository, ObjBuilding.class);
	}

}
