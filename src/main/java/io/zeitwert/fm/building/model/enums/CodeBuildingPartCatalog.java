
package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;

import java.util.List;

import org.flywaydb.core.internal.util.Pair;

public class CodeBuildingPartCatalog extends EnumeratedBase {

	private final String parts;

	public CodeBuildingPartCatalog(CodeBuildingPartCatalogEnum enumeration, String id, String name, String parts) {
		super(enumeration, id, name);
		this.parts = parts;
	}

	public List<Pair<CodeBuildingPart, Integer>> getPartList() {
		List<String> parts = List.of(this.parts.split(","));
		return parts.stream().map(p -> {
			String[] partWeight = (p.indexOf(":") >= 0 ? p : p + ":" + 0).split(":");
			CodeBuildingPart part = CodeBuildingPartEnum.getBuildingPart(partWeight[0]);
			Integer weight = Integer.parseInt(partWeight[1]);
			return Pair.of(part, weight);
		}).toList();
	}

}
