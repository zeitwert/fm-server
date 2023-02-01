
package io.zeitwert.fm.building.model;

import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingVRecord;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjBuildingRepository extends FMObjRepository<ObjBuilding, ObjBuildingVRecord> {

	ObjContactRepository getContactRepository();

	ObjDocumentRepository getDocumentRepository();

	ObjBuildingPartRatingRepository getRatingRepository();

	ObjBuildingPartElementRatingRepository getElementRepository();

}
