
package io.zeitwert.fm.building.model;

import io.dddrive.core.obj.model.ObjPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;

public interface ObjBuildingPartElementRating extends ObjPart<ObjBuilding> {

	CodeBuildingPart getBuildingPart();

	void setBuildingPart(CodeBuildingPart buildingPart);

	Integer getWeight();

	void setWeight(Integer weight);

	Integer getCondition();

	void setCondition(Integer condition);

	Integer getRatingYear();

	void setRatingYear(Integer year);

	Integer getStrain();

	void setStrain(Integer strain);

	Integer getStrength();

	void setStrength(Integer strength);

	String getDescription();

	void setDescription(String description);

	String getConditionDescription();

	void setConditionDescription(String description);

	String getMeasureDescription();

	void setMeasureDescription(String description);

	// Set<CodeBuildingElementDescription> getMaterialDescriptionSet();

	// void clearMaterialDescriptionSet();

	// void addMaterialDescription(CodeBuildingElementDescription description);

	// void removeMaterialDescription(CodeBuildingElementDescription description);

	// Set<CodeBuildingElementDescription> getConditionDescriptionSet();

	// void clearConditionDescriptionSet();

	// void addConditionDescription(CodeBuildingElementDescription description);

	// void removeConditionDescription(CodeBuildingElementDescription description);

	// Set<CodeBuildingElementDescription> getMeasureDescriptionSet();

	// void clearMeasureDescriptionSet();

	// void addMeasureDescription(CodeBuildingElementDescription description);

	// void removeMeasureDescription(CodeBuildingElementDescription description);

	Integer getCondition(Integer year);

}
