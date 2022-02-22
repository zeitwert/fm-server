
package fm.comunas.fm.building.model.enums;

import fm.comunas.ddd.enums.model.base.EnumeratedBase;

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
