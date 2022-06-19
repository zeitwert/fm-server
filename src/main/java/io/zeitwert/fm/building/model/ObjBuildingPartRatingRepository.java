
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.obj.model.ObjPartRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

public interface ObjBuildingPartRatingRepository
		extends ObjPartRepository<ObjBuilding, ObjBuildingPartRating> {

	CodePartListType getElementListType();

}
