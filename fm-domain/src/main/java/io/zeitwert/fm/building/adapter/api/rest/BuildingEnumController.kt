package io.zeitwert.fm.building.adapter.api.rest

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingPartWeightDto
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("buildingEnumController")
@RequestMapping("/enum")
class BuildingEnumController {

	@GetMapping("/building/codeBuildingSubType/{buildingTypeId}")
	fun getBuildingSubTypes(
		@PathVariable buildingTypeId: String,
	): ResponseEntity<List<EnumeratedDto>> {
		try {
			val buildingType = CodeBuildingType.getBuildingType(buildingTypeId)
			val subTypes = CodeBuildingSubType.items.filter { st -> st.buildingType == buildingType }
			return ResponseEntity.ok().body(subTypes.map { EnumeratedDto.of(it)!! })
		} catch (e: Exception) {
			return ResponseEntity.notFound().build()
		}
	}

	@GetMapping("/building/codeBuildingPartCatalog/{partCatalogId}")
	fun getPartCatalog(
		@PathVariable partCatalogId: String,
	): ResponseEntity<List<BuildingPartWeightDto>?> {
		try {
			val partCatalog = CodeBuildingPartCatalog.getPartCatalog(partCatalogId)!!
			return ResponseEntity
				.ok()
				.body(
					partCatalog
						.getParts()
						.map {
							BuildingPartWeightDto(
								part = EnumeratedDto.of(it.first)!!,
								weight = it.second,
								lifeTime20 = it.first.getLifetime(0.2),
								lifeTime50 = it.first.getLifetime(0.5),
								lifeTime70 = it.first.getLifetime(0.7),
								lifeTime85 = it.first.getLifetime(0.85),
								lifeTime95 = it.first.getLifetime(0.95),
								lifeTime100 = it.first.getLifetime(1.0),
							)
						},
				)
		} catch (e: Exception) {
			return ResponseEntity.notFound().build()
		}
	}

}
