package io.zeitwert.fm.building.model

import dddrive.app.obj.model.ObjPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPart

interface ObjBuildingPartElementRating : ObjPart<ObjBuilding> {

	var buildingPart: CodeBuildingPart?

	var weight: Int?

	var condition: Int?

	var ratingYear: Int?

	var strain: Int?

	var strength: Int?

	var description: String?

	var conditionDescription: String?

	var measureDescription: String?

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

	fun getCondition(year: Int): Int

}
