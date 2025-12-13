
package io.zeitwert.fm.building.adapter.api.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingPartWeightDto;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType;
import io.zeitwert.fm.building.model.enums.CodeBuildingType;

@RestController("buildingEnumController")
@RequestMapping("/enum")
public class BuildingEnumController {

	@GetMapping("/building/codeBuildingSubType/{buildingTypeId}")
	public ResponseEntity<List<CodeBuildingSubType>> getBuildingSubTypes(@PathVariable String buildingTypeId) {
		CodeBuildingType buildingType = CodeBuildingType.getBuildingType(buildingTypeId);
		if (buildingType == null) {
			return ResponseEntity.notFound().build();
		}
		List<CodeBuildingSubType> subTypes = Arrays.stream(CodeBuildingSubType.values())
				.filter(st -> st.getBuildingType() == buildingType)
				.toList();
		return ResponseEntity.ok().body(subTypes);
	}

	@GetMapping("/building/codeBuildingPartCatalog/{partCatalogId}")
	public ResponseEntity<List<BuildingPartWeightDto>> getPartCatalog(@PathVariable String partCatalogId) {
		CodeBuildingPartCatalog partCatalog = CodeBuildingPartCatalog.getPartCatalog(partCatalogId);
		if (partCatalog == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok()
				.body(
						partCatalog.getParts().stream().map(p -> {
							return BuildingPartWeightDto.builder()
									.part(EnumeratedDto.of(p.getFirst()))
									.weight(p.getSecond())
									.lifeTime20(p.getFirst().getLifetime(0.2))
									.lifeTime50(p.getFirst().getLifetime(0.5))
									.lifeTime70(p.getFirst().getLifetime(0.7))
									.lifeTime85(p.getFirst().getLifetime(0.85))
									.lifeTime95(p.getFirst().getLifetime(0.95))
									.lifeTime100(p.getFirst().getLifetime(1.0))
									.build();
						}).toList());
	}

}
