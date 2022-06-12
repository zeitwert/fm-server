
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjBuildingRepository extends FMObjRepository<ObjBuilding, ObjBuildingVRecord> {

	ObjBuildingPartElementRepository getElementRepository();

	CodePartListType getElementListType();

	CodePartListType getMaterialDescriptionSetType();

	CodePartListType getConditionDescriptionSetType();

	CodePartListType getMeasureDescriptionSetType();

}
