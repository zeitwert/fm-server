
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjBuildingRepository extends FMObjRepository<ObjBuilding, ObjBuildingVRecord> {

	ObjContactRepository getContactRepository();

	ObjDocumentRepository getDocumentRepository();

	ObjBuildingPartRatingRepository getRatingRepository();

	CodePartListType getContactSetType();

	CodePartListType getRatingListType();

	ObjBuildingPartElementRatingRepository getElementRepository();

	CodePartListType getMaterialDescriptionSetType();

	CodePartListType getConditionDescriptionSetType();

	CodePartListType getMeasureDescriptionSetType();

}
