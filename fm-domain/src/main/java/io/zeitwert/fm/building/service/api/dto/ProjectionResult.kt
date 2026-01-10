package io.zeitwert.fm.building.service.api.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import io.zeitwert.app.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating

data class ProjectionResult(
	val startYear: Int,
	val duration: Int,
	val elementList: List<ProjectionElement>,
	val periodList: List<ProjectionPeriod> = emptyList(),
	@JsonIgnore val elementMap: Map<EnumeratedDto, ObjBuildingPartElementRating> = emptyMap(),
	@JsonIgnore val elementResultMap: Map<String, List<ProjectionPeriod>> = emptyMap(),
) {

	val endYear: Int
		get() = startYear + duration

	fun getElement(enumerated: EnumeratedDto): ObjBuildingPartElementRating = elementMap[enumerated]!!

	fun getBuilding(enumerated: EnumeratedDto): ObjBuilding = getElement(enumerated)!!.meta.aggregate as ObjBuilding

}

data class ProjectionElement(
	val element: EnumeratedDto,
	val building: EnumeratedDto,
	val buildingPart: EnumeratedDto,
	val restorationCosts: Double = 0.0,
)

data class ProjectionPeriod(
	val year: Int,
	val originalValue: Double,
	val timeValue: Double,
	val restorationCosts: Double,
	val restorationElements: List<ProjectionElement> = emptyList(),
	val techPart: Double = 0.0,
	val techRate: Double = 0.0,
	val maintenanceRate: Double = 0.0,
	val maintenanceCosts: Double = 0.0,
)
