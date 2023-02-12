
package io.zeitwert.fm.building.model;

import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;

public interface ObjBuildingRepository extends ObjRepository<ObjBuilding, ObjBuildingVRecord> {

	static CodePartListType ratingListType() {
		return CodePartListTypeEnum.getPartListType("building.ratingList");
	}

	static CodePartListType contactSetType() {
		return CodePartListTypeEnum.getPartListType("building.contactSet");
	}

	default ObjContactRepository getContactRepository() {
		return this.getAppContext().getBean(ObjContactRepository.class);
	}

	default ObjDocumentRepository getDocumentRepository() {
		return this.getAppContext().getBean(ObjDocumentRepository.class);
	}

	default ObjBuildingPartRatingRepository getRatingRepository() {
		return this.getAppContext().getBean(ObjBuildingPartRatingRepository.class);
	}

	default ObjBuildingPartElementRatingRepository getElementRepository() {
		return this.getAppContext().getBean(ObjBuildingPartElementRatingRepository.class);
	}

}
