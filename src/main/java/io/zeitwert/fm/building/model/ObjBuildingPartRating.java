
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;

import java.time.LocalDate;
import java.util.List;

public interface ObjBuildingPartRating extends ObjPart<ObjBuilding> {

	CodeBuildingRatingStatus getRatingStatus();

	void setRatingStatus(CodeBuildingRatingStatus ratingStatus);

	LocalDate getRatingDate();

	void setRatingDate(LocalDate ratingDate);

	ObjUser getRatingUser();

	void setRatingUser(ObjUser ratingUser);

	CodeBuildingPartCatalog getBuildingPartCatalog();

	void setBuildingPartCatalog(CodeBuildingPartCatalog buildingPartCatalog);

	CodeBuildingMaintenanceStrategy getBuildingMaintenanceStrategy();

	void setBuildingMaintenanceStrategy(CodeBuildingMaintenanceStrategy strategy);

	Integer getElementContributions();

	Integer getElementCount();

	ObjBuildingPartElementRating getElement(Integer seqNr);

	List<ObjBuildingPartElementRating> getElementList();

	ObjBuildingPartElementRating getElementById(Integer elementId);

	ObjBuildingPartElementRating getElement(CodeBuildingPart buildingPart);

	void clearElementList();

	ObjBuildingPartElementRating addElement(CodeBuildingPart buildingPart);

	void removeElement(Integer elementId);

}
