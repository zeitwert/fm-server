
package io.zeitwert.fm.building.model.enums;

import io.zeitwert.ddd.enums.model.base.EnumeratedBase;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

import org.flywaydb.core.internal.util.Pair;

@Data
@SuperBuilder
public class CodeBuildingPartCatalog extends EnumeratedBase {

	private final String parts;

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public List<Pair<CodeBuildingPart, Integer>> getParts() {
		List<String> parts = List.of(this.parts.split(","));
		return parts.stream().map(p -> {
			String[] partWeight = (p.indexOf(":") >= 0 ? p : p + ":" + 0).split(":");
			CodeBuildingPart part = CodeBuildingPartEnum.getBuildingPart(partWeight[0]);
			Integer weight = Integer.parseInt(partWeight[1]);
			return Pair.of(part, weight);
		}).toList();
	}

}
