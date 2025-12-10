
package io.zeitwert.fm.building.model.enums;

import io.dddrive.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CodeBuildingSubType extends EnumeratedBase {

	private final CodeBuildingType buildingType;

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public CodeBuildingType getBuildingType() {
		return this.buildingType;
	}

}
