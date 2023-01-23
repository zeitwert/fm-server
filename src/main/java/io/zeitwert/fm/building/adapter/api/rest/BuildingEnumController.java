
package io.zeitwert.fm.building.adapter.api.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingPartWeightDto;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingType;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;

@RestController("buildingEnumController")
@RequestMapping("/enum")
public class BuildingEnumController {

	@GetMapping("/building/codeBuildingSubType/{buildingTypeId}")
	public ResponseEntity<List<CodeBuildingSubType>> getBuildingSubTypes(@PathVariable String buildingTypeId) {
		CodeBuildingType buildingType = CodeBuildingTypeEnum.getBuildingType(buildingTypeId);
		if (buildingType == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(CodeBuildingSubTypeEnum.getBuildingSubTypes(buildingType));
	}

	@GetMapping("/building/codeBuildingPartCatalog/{partCatalogId}")
	public ResponseEntity<List<BuildingPartWeightDto>> getPartCatalog(@PathVariable String partCatalogId) {
		CodeBuildingPartCatalog partCatalog = CodeBuildingPartCatalogEnum.getPartCatalog(partCatalogId);
		if (partCatalog == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok()
				.body(
						partCatalog.getParts().stream().map(p -> {
							return BuildingPartWeightDto.builder()
									.part(EnumeratedDto.fromEnum(p.getLeft()))
									.weight(p.getRight())
									.lifeTime20(p.getLeft().getLifetime(0.2))
									.lifeTime50(p.getLeft().getLifetime(0.5))
									.lifeTime70(p.getLeft().getLifetime(0.7))
									.lifeTime85(p.getLeft().getLifetime(0.85))
									.lifeTime95(p.getLeft().getLifetime(0.95))
									.lifeTime100(p.getLeft().getLifetime(1.0))
									.build();
						}).toList());
	}

}
