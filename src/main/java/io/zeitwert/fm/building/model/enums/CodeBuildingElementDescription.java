
package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;

public class CodeBuildingElementDescription extends EnumeratedBase {

	private final String category;

	public CodeBuildingElementDescription(CodeBuildingElementDescriptionEnum enumeration, String id, String name,
			String category) {
		super(enumeration, id, name);
		this.category = category;
	}

	public String getCategory() {
		return this.category;
	}

}
