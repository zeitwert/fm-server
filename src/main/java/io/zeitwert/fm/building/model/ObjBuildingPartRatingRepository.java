
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.obj.model.ObjPartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;

public interface ObjBuildingPartRatingRepository
		extends ObjPartRepository<ObjBuilding, ObjBuildingPartRating> {

	static public CodePartListType getElementListType() {
		return CodePartListTypeEnum.getPartListType("building.elementRatingList");
	}

}
