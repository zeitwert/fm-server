
package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class CodeBuildingElementDescription extends EnumeratedBase {

	private final String category;

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public String getCategory() {
		return this.category;
	}

}
