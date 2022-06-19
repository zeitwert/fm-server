
package io.zeitwert.fm.building.model;

import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingElementDescription;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;

import java.util.Set;

public interface ObjBuildingPartElementRating extends ObjPart<ObjBuilding> {

	CodeBuildingPart getBuildingPart();

	void setBuildingPart(CodeBuildingPart buildingPart);

	Integer getValuePart();

	void setValuePart(Integer valuePart);

	Integer getCondition();

	void setCondition(Integer condition);

	Integer getConditionYear();

	void setConditionYear(Integer condition);

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

	Set<CodeBuildingElementDescription> getMaterialDescriptionSet();

	void clearMaterialDescriptionSet();

	void addMaterialDescription(CodeBuildingElementDescription description);

	void removeMaterialDescription(CodeBuildingElementDescription description);

	Set<CodeBuildingElementDescription> getConditionDescriptionSet();

	void clearConditionDescriptionSet();

	void addConditionDescription(CodeBuildingElementDescription description);

	void removeConditionDescription(CodeBuildingElementDescription description);

	Set<CodeBuildingElementDescription> getMeasureDescriptionSet();

	void clearMeasureDescriptionSet();

	void addMeasureDescription(CodeBuildingElementDescription description);

	void removeMeasureDescription(CodeBuildingElementDescription description);

}
