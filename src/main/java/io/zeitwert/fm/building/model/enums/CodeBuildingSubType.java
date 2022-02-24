
package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;

public class CodeBuildingSubType extends EnumeratedBase {

	private final CodeBuildingType buildingType;

	public CodeBuildingSubType(CodeBuildingSubTypeEnum enumeration, String id, String name,
			CodeBuildingType buildingType) {
		super(enumeration, id, name);
		this.buildingType = buildingType;
	}

	public CodeBuildingType getBuildingType() {
		return this.buildingType;
	}

}
