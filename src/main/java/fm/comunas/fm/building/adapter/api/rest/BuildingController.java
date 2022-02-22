
package fm.comunas.fm.building.adapter.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.fm.building.adapter.api.rest.dto.BuildingPartWeightDto;
import fm.comunas.fm.building.model.enums.CodeBuildingPartCatalog;
import fm.comunas.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import fm.comunas.fm.building.model.enums.CodeBuildingSubType;
import fm.comunas.fm.building.model.enums.CodeBuildingSubTypeEnum;
import fm.comunas.fm.building.model.enums.CodeBuildingType;
import fm.comunas.fm.building.model.enums.CodeBuildingTypeEnum;
import fm.comunas.fm.building.service.api.ProjectionService;

@RestController("buildingController")
@RequestMapping("/enum")
public class BuildingController {

	@Autowired
	private ProjectionService projectionService;

	@GetMapping("/building/codeBuildingSubType/{buildingTypeId}")
	public ResponseEntity<List<CodeBuildingSubType>> getBuildingSubTypeList(@PathVariable String buildingTypeId) {
		CodeBuildingType buildingType = CodeBuildingTypeEnum.getBuildingType(buildingTypeId);
		if (buildingType == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(CodeBuildingSubTypeEnum.getBuildingSubTypeList(buildingType));
	}

	@GetMapping("/building/codeBuildingPartCatalog/{buildingPartCatalogId}")
	public ResponseEntity<List<BuildingPartWeightDto>> getBuildingPartCatalog(
			@PathVariable String buildingPartCatalogId) {
		CodeBuildingPartCatalog buildingPartCatalog = CodeBuildingPartCatalogEnum
				.getBuildingPartCatalog(buildingPartCatalogId);
		if (buildingPartCatalog == null) {
			return ResponseEntity.notFound().build();
		}
		//@formatter:off
		return ResponseEntity.ok().body(buildingPartCatalog.getPartList().stream().map(p -> {
			return BuildingPartWeightDto.builder()
				.part(EnumeratedDto.fromEnum(p.getLeft()))
				.weight(p.getRight())
				.lifeTime20(projectionService.getLifetime(p.getLeft(), 0.2))
				.lifeTime50(projectionService.getLifetime(p.getLeft(), 0.5))
				.lifeTime70(projectionService.getLifetime(p.getLeft(), 0.7))
				.lifeTime85(projectionService.getLifetime(p.getLeft(), 0.85))
				.lifeTime95(projectionService.getLifetime(p.getLeft(), 0.95))
				.lifeTime100(projectionService.getLifetime(p.getLeft(), 1.0))
				.build();
		}).toList());
		//@formatter:on
	}

}
