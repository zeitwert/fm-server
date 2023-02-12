
package io.zeitwert.fm.building.model;

import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.obj.model.ObjPartRepository;

public interface ObjBuildingPartRatingRepository
		extends ObjPartRepository<ObjBuilding, ObjBuildingPartRating> {

	static public CodePartListType getElementListType() {
		return CodePartListTypeEnum.getPartListType("building.elementRatingList");
	}

}
