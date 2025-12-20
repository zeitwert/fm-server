package io.zeitwert.fm.building.adapter.api.rest

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto.Companion.of
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingPartWeightDto
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType.Enumeration.getBuildingType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController("buildingEnumController")
@RequestMapping("/enum")
class BuildingEnumController {

	@GetMapping("/building/codeBuildingSubType/{buildingTypeId}")
	fun getBuildingSubTypes(@PathVariable buildingTypeId: String): ResponseEntity<MutableList<CodeBuildingSubType?>?> {
		val buildingType = getBuildingType(buildingTypeId)
		if (buildingType == null) {
			return ResponseEntity.notFound().build<MutableList<CodeBuildingSubType?>?>()
		}
		val subTypes = Arrays.stream<CodeBuildingSubType>(CodeBuildingSubType.entries.toTypedArray())
			.filter { st: CodeBuildingSubType? -> st!!.buildingType == buildingType }
			.toList()
		return ResponseEntity.ok().body<MutableList<CodeBuildingSubType?>?>(subTypes)
	}

	@GetMapping("/building/codeBuildingPartCatalog/{partCatalogId}")
	fun getPartCatalog(@PathVariable partCatalogId: String): ResponseEntity<MutableList<BuildingPartWeightDto?>?> {
		val partCatalog = CodeBuildingPartCatalog.getPartCatalog(partCatalogId)
		if (partCatalog == null) {
			return ResponseEntity.notFound().build<MutableList<BuildingPartWeightDto?>?>()
		}
		return ResponseEntity.ok()
			.body<MutableList<BuildingPartWeightDto?>?>(
				partCatalog.getParts().stream().map<BuildingPartWeightDto?> { p: Pair<CodeBuildingPart?, Int?>? ->
					BuildingPartWeightDto(
						part = of(p!!.first),
						weight = p.second,
						lifeTime20 = p.first!!.getLifetime(0.2),
						lifeTime50 = p.first!!.getLifetime(0.5),
						lifeTime70 = p.first!!.getLifetime(0.7),
						lifeTime85 = p.first!!.getLifetime(0.85),
						lifeTime95 = p.first!!.getLifetime(0.95),
						lifeTime100 = p.first!!.getLifetime(1.0),
					)
				}.toList()
			)
	}

}
