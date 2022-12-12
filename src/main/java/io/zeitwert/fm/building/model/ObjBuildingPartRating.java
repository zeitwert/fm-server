
package io.zeitwert.fm.building.model;

import java.time.LocalDate;
import java.util.List;

import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;

public interface ObjBuildingPartRating extends ObjPart<ObjBuilding> {

	CodeBuildingRatingStatus getRatingStatus();

	void setRatingStatus(CodeBuildingRatingStatus ratingStatus);

	LocalDate getRatingDate();

	Integer getRatingYear();

	void setRatingDate(LocalDate ratingDate);

	ObjUser getRatingUser();

	void setRatingUser(ObjUser ratingUser);

	CodeBuildingPartCatalog getPartCatalog();

	void setPartCatalog(CodeBuildingPartCatalog partCatalog);

	CodeBuildingMaintenanceStrategy getMaintenanceStrategy();

	void setMaintenanceStrategy(CodeBuildingMaintenanceStrategy strategy);

	Integer getElementCount();

	ObjBuildingPartElementRating getElement(Integer seqNr);

	List<ObjBuildingPartElementRating> getElementList();

	ObjBuildingPartElementRating getElementById(Integer elementId);

	ObjBuildingPartElementRating getElement(CodeBuildingPart buildingPart);

	void clearElementList();

	ObjBuildingPartElementRating addElement(CodeBuildingPart buildingPart);

	void removeElement(Integer elementId);

	Integer getElementWeights();

	Integer getCondition();

	Integer getCondition(Integer year);

}
