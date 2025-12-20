package io.zeitwert.fm.portfolio.service.api.impl

import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.service.api.ProjectionService
import io.zeitwert.fm.building.service.api.dto.EvaluationBuilding
import io.zeitwert.fm.building.service.api.dto.EvaluationElement
import io.zeitwert.fm.building.service.api.dto.EvaluationPeriod
import io.zeitwert.fm.building.service.api.impl.BuildingEvaluationServiceImpl.Companion.SOFT_RETURN
import io.zeitwert.fm.building.service.api.impl.BuildingEvaluationServiceImpl.Companion.getConditionColor
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.service.api.PortfolioEvaluationService
import io.zeitwert.fm.portfolio.service.api.dto.PortfolioEvaluationResult
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.stream.Collectors

@Service("portfolioEvaluationService")
class PortfolioEvaluationServiceImpl(
	private val buildingRepository: ObjBuildingRepository,
	private val projectionService: ProjectionService
) : PortfolioEvaluationService {

	override fun getEvaluation(portfolio: ObjPortfolio): PortfolioEvaluationResult? {
		val buildings = portfolio.buildingSet.stream().map<ObjBuilding?> { id: Any? -> this.buildingRepository.get(id!!) }
			.collect(Collectors.toSet())
		val projectionResult = this.projectionService.getProjection(
			buildings,
			ProjectionService.DefaultDuration
		)

		val buildingList: MutableList<EvaluationBuilding> = mutableListOf()
		val maxInsuredValue = buildings.stream().map { b: ObjBuilding? -> b!!.insuredValue!!.toInt() }
			.reduce(0) { max: Int?, b: Int? -> if (b!! > max!!) b else max }
		for (building in buildings) {
			val rating = building.currentRating
			val ratingYear = if (rating != null) rating.ratingDate!!.year else null
			val evaluationBuilding = EvaluationBuilding(
				id = building.id as Int,
				name = building.name,
				description = building.description,
				buildingNr = building.buildingNr,
				address = building.street + " " + building.zip + " " + building.city,
				insuredValue = building.insuredValue?.toInt(),
				relativeValue = (building.insuredValue!!.toInt() / maxInsuredValue.toDouble() * 100.0) as Int,
				insuredValueYear = building.insuredValueYear!!,
				ratingYear = ratingYear!!,
				condition = building.getCondition(2023)!!,
				conditionColor = getConditionColor(building.getCondition(2023))!!
			)
			buildingList.add(evaluationBuilding)
		}
		buildingList.sortWith(Comparator { obj: EvaluationBuilding?, other: EvaluationBuilding? -> obj!!.compareTo(other!!) })

		// List of evaluation elements, grouped by building part, with summed up
		// restoration costs
		val currentYear = LocalDate.now().year
		val elementMap: MutableMap<String, EvaluationElement> = mutableMapOf()
		for (period in projectionResult.periodList) {
			for (element in period.restorationElements) {
				var ee = elementMap.get(element.buildingPart.id)
				if (ee == null) {
					ee = EvaluationElement(
						name = element.buildingPart.name,
						shortTermCosts = 0,
						midTermCosts = 0,
						longTermCosts = 0,
					)
					elementMap.put(element.buildingPart.id!!, ee)
				}
				if (period.year <= currentYear + 1) {
					ee.shortTermCosts += element.restorationCosts.toInt()
				} else if (period.year <= currentYear + 4) {
					ee.midTermCosts += element.restorationCosts.toInt()
				} else if (period.year <= currentYear + 25) {
					ee.longTermCosts += element.restorationCosts.toInt()
				}
			}
		}

		val elements: MutableList<EvaluationElement> = mutableListOf()
		for (buildingPart in CodeBuildingPart.Enumeration.items) {
			val ee = elementMap.get(buildingPart.id)
			if (ee != null) {
				elements.add(ee)
			}
		}

		val periods: MutableList<EvaluationPeriod> = mutableListOf()
		var aggrCosts = 0
		for (pp in projectionResult.periodList) {
			val totalCosts = (pp.maintenanceCosts + pp.restorationCosts).toInt()
			aggrCosts += totalCosts
			var restorationElement: String? = ""
			if (pp.restorationElements.size == 1) {
				restorationElement = pp.restorationElements.get(0).buildingPart.name
			}
			val eps = EvaluationPeriod(
				year = pp.year,
				originalValue = pp.originalValue.toInt(),
				timeValue = pp.timeValue.toInt(),
				maintenanceCosts = pp.maintenanceCosts.toInt(),
				restorationCosts = pp.restorationCosts.toInt(),
				restorationElement = restorationElement!!,
				restorationBuilding = "",
				totalCosts = totalCosts,
				aggrCosts = aggrCosts,
			)
			periods.add(eps)
			if (pp.restorationElements.size > 1) {
				for (re in pp.restorationElements) {
					val epd = EvaluationPeriod(
						restorationBuilding = re.building.name!!,
						restorationElement = re.buildingPart.name!!,
						restorationCosts = re.restorationCosts.toInt(),
					)
					periods.add(epd)
				}
			}
		}

		return PortfolioEvaluationResult(
			id = portfolio.id as Int,
			name = portfolio.name ?: "",
			description = this.replaceEol(portfolio.description),
			accountName = portfolio.account?.name ?: "",
			buildings = buildingList,
			elements = elements,
			startYear = projectionResult.startYear,
			periods = periods,
		)
	}

	private fun replaceEol(text: String?): String {
		return text?.replace("<br>", SOFT_RETURN) ?: ""
	}

}
