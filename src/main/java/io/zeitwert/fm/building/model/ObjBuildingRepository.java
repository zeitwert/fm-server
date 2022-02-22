
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;

public interface ObjBuildingRepository extends ObjRepository<ObjBuilding, ObjBuildingVRecord> {

	ObjBuildingPartElementRepository getElementRepository();

	CodePartListType getElementListType();

	CodePartListType getMaterialDescriptionSetType();

	CodePartListType getConditionDescriptionSetType();

	CodePartListType getMeasureDescriptionSetType();

}
