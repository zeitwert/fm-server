package io.zeitwert.fm.building.model.impl;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRatingRepository;
import io.zeitwert.fm.building.model.base.ObjBuildingPartElementRatingBase;

@Component("buildingPartElementRatingRepository")
public class ObjBuildingPartElementRatingRepositoryImpl
		extends ObjPartRepositoryBase<ObjBuilding, ObjBuildingPartElementRating>
		implements ObjBuildingPartElementRatingRepository {

	private static final String PART_TYPE = "obj_building_part_element_rating";

	protected ObjBuildingPartElementRatingRepositoryImpl(AppContext appContext) {
		super(ObjBuilding.class, ObjBuildingPartElementRating.class, ObjBuildingPartElementRatingBase.class, PART_TYPE,
				appContext);
	}

}
