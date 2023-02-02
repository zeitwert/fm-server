
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjBuildingRepository extends FMObjRepository<ObjBuilding, ObjBuildingVRecord> {

	static CodePartListType ratingListType() {
		return CodePartListTypeEnum.getPartListType("building.ratingList");
	}

	static CodePartListType contactSetType() {
		return CodePartListTypeEnum.getPartListType("building.contactSet");
	}

	ObjContactRepository getContactRepository();

	ObjDocumentRepository getDocumentRepository();

	ObjBuildingPartRatingRepository getRatingRepository();

	ObjBuildingPartElementRatingRepository getElementRepository();

}
