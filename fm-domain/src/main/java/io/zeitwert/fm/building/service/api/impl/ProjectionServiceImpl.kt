package io.zeitwert.fm.building.service.api.impl

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto.Companion.of
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.enums.CodeBuildingPart.Enumeration.getMaintenanceRate
import io.zeitwert.fm.building.model.enums.CodeBuildingPart.Enumeration.getTechRate
import io.zeitwert.fm.building.service.api.ProjectionService
import io.zeitwert.fm.building.service.api.dto.ProjectionElement
import io.zeitwert.fm.building.service.api.dto.ProjectionPeriod
import io.zeitwert.fm.building.service.api.dto.ProjectionResult
import io.zeitwert.fm.util.NumericUtils
import org.springframework.stereotype.Service

@Service("projectionService")
class ProjectionServiceImpl : ProjectionService {

	fun getProjection(building: ObjBuilding, duration: Int): ProjectionResult {
		return this.getProjection(setOf(building), duration)
	}

	/**
	 * - get startYear (max(building.element.ratingYear))
	 * - for all elements of all buildings:
	 * -
	 *
	 * @param buildings
	 * @param duration
	 * @return
	 */
	override fun getProjection(buildings: Set<ObjBuilding>, duration: Int): ProjectionResult {
		val elementList: MutableList<ProjectionElement> = mutableListOf()
		val elementMap: MutableMap<EnumeratedDto, ObjBuildingPartElementRating> = mutableMapOf()
		val elementResultMap: MutableMap<String, List<ProjectionPeriod>> = mutableMapOf()
		val startYear = this.getMinProjectionDate(buildings)
		for (building in buildings) {
			val buildingEnum = this.getAsEnumerated(building)
			if (building.currentRating != null) {
				for (element in building.currentRating!!.elementList) {
					val elementEnum = this.getAsEnumerated(element)
					val buildingPartEnum = of(element.buildingPart)!!
					if (element.weight != null && element.ratingYear != null) {
						if (element.weight!! > 0 && element.condition!! > 0) {
							val elementPeriodList =
								element.buildingPart!!.getProjection(
									/* elementValue => */
									100.0, /* ratingYear => */
									element.ratingYear!!, /* condition => */
									element.condition!! / 100.0, /* startYear => */
									startYear, /* duration => */
									duration
								)
							elementList.add(
								ProjectionElement(
									element = elementEnum,
									building = buildingEnum,
									buildingPart = buildingPartEnum,
								),
							)
							elementMap.put(elementEnum, element)
							elementResultMap.put(elementEnum.id!!, elementPeriodList)
						}
					}
				}
			}
		}
		val rawResult =
			ProjectionResult(
				startYear = startYear,
				duration = duration,
				elementList = elementList,
				elementMap = elementMap,
				elementResultMap = elementResultMap,
			)
		return this.consolidateProjection(rawResult)
	}

	private fun getMinProjectionDate(buildings: Set<ObjBuilding>): Int {
		var projectionYear = 0
		for (building in buildings) {
			val minProjYear = this.getMinProjectionDate(building)
			if (minProjYear > projectionYear) {
				projectionYear = minProjYear
			}
		}
		return projectionYear
	}

	private fun getMinProjectionDate(building: ObjBuilding): Int {
		var projectionYear: Int = building.insuredValueYear!!
		if (building.currentRating != null) {
			for (element in building.currentRating!!.elementList) {
				if (element.weight != null && element.weight!! > 0) {
					if (element.ratingYear != null && element.ratingYear!! > projectionYear) {
						projectionYear = element.ratingYear!!
					}
				}
			}
		}
		return projectionYear
	}

	private fun getAsEnumerated(building: ObjBuilding): EnumeratedDto {
		val id: String? = building.id.toString()
		return of(id, building.name)
	}

	private fun getAsEnumerated(element: ObjBuildingPartElementRating): EnumeratedDto {
		val id = element.id.toString()
		return of(id, element.meta.aggregate.name + ": " + element.buildingPart!!.getName())
	}

	private fun consolidateProjection(projectionResult: ProjectionResult): ProjectionResult {
		val buildingPeriodList: MutableList<ProjectionPeriod> = mutableListOf()

		var techPart = 0.0
		for (part in projectionResult.elementMap.values) {
			techPart += part.weight!! / 100 * part.buildingPart!!.getTechRate()
		}
		val techRate = getTechRate(techPart)

		for (year in projectionResult.startYear..projectionResult.endYear) {
			var originalValue = 0.0
			var timeValue = 0.0
			var restorationCosts = 0.0
			val restorationElements: MutableList<ProjectionElement?> = ArrayList<ProjectionElement?>()

			for (elementEnum in projectionResult.elementMap.keys) {
				val element = projectionResult.getElement(elementEnum)
				val elementPeriods: List<ProjectionPeriod> = projectionResult.elementResultMap[elementEnum.id]!!
				val elementPeriod = elementPeriods[year - elementPeriods[0].year]
				val building = projectionResult.getBuilding(elementEnum)
				val buildingValue = building.getBuildingValue(year)
				val elementValue = buildingValue * element.weight!! / 100.0
				originalValue += elementValue
				timeValue += elementValue * elementPeriod.timeValue / 100.0
				var elementRestorationCosts = elementValue * elementPeriod.restorationCosts / 100.0
				elementRestorationCosts = NumericUtils.roundProgressive(elementRestorationCosts)
				restorationCosts += elementRestorationCosts
				if (elementRestorationCosts != 0.0) {
					val buildingEnum = this.getAsEnumerated(building)
					val buildingPartEnum = of(element.buildingPart)!!
					val restorationElement =
						ProjectionElement(
							element = elementEnum,
							building = buildingEnum,
							buildingPart = buildingPartEnum,
							restorationCosts = elementRestorationCosts,
						)
					restorationElements.add(restorationElement)
				}
			}

			val maintenanceRate = techRate * getMaintenanceRate(timeValue / originalValue) / 100.0
			var maintenanceCosts = maintenanceRate * originalValue

			originalValue = NumericUtils.roundProgressive(originalValue)
			timeValue = NumericUtils.roundProgressive(timeValue)
			restorationCosts = NumericUtils.roundProgressive(restorationCosts)
			maintenanceCosts = NumericUtils.roundProgressive(maintenanceCosts)

			val buildingPeriod =
				ProjectionPeriod(
					year = year,
					originalValue = originalValue,
					timeValue = timeValue,
					restorationCosts = restorationCosts,
					restorationElements = restorationElements.filterNotNull(),
					techPart = techPart,
					techRate = techRate,
					maintenanceRate = maintenanceRate,
					maintenanceCosts = maintenanceCosts,
				)
			buildingPeriodList.add(buildingPeriod)
		}

		return ProjectionResult(
			startYear = projectionResult.startYear,
			duration = projectionResult.duration,
			elementList = projectionResult.elementList,
			elementMap = projectionResult.elementMap,
			elementResultMap = projectionResult.elementResultMap,
			periodList = buildingPeriodList,
		)
	}
}
