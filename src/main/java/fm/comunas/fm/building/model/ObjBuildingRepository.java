
package fm.comunas.fm.building.model;

import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.fm.building.model.db.tables.records.ObjBuildingVRecord;

public interface ObjBuildingRepository extends ObjRepository<ObjBuilding, ObjBuildingVRecord> {

	ObjBuildingPartElementRepository getElementRepository();

	CodePartListType getElementListType();

	CodePartListType getMaterialDescriptionSetType();

	CodePartListType getConditionDescriptionSetType();

	CodePartListType getMeasureDescriptionSetType();

}
