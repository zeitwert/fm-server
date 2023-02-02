package io.zeitwert.fm.building.model.impl;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingPartRatingBase;

@Component("buildingPartRatingRepository")
public class ObjBuildingPartRatingRepositoryImpl
		extends ObjPartRepositoryBase<ObjBuilding, ObjBuildingPartRating>
		implements ObjBuildingPartRatingRepository {

	private static final String PART_TYPE = "obj_building_part_element_rating";

	protected ObjBuildingPartRatingRepositoryImpl(AppContext appContext) {
		super(ObjBuilding.class, ObjBuildingPartRating.class, ObjBuildingPartRatingBase.class, PART_TYPE, appContext);
	}

}
