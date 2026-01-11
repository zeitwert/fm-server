package io.zeitwert.fm.building.api.impl

import io.zeitwert.fm.building.api.BuildingEvaluationService
import io.zeitwert.fm.building.api.ProjectionService
import io.zeitwert.fm.building.api.dto.BuildingEvaluationResult
import io.zeitwert.fm.building.api.dto.EvaluationElement
import io.zeitwert.fm.building.api.dto.EvaluationParameter
import io.zeitwert.fm.building.api.dto.EvaluationPeriod
import io.zeitwert.fm.building.api.dto.ProjectionResult
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.util.Formatter
import io.zeitwert.fm.util.NumericUtils
import org.jooq.tools.StringUtils
import org.springframework.stereotype.Service
import java.awt.Color
import java.util.Set

@Service("buildingEvaluationService")
class BuildingEvaluationServiceImpl(
	val projectionService: ProjectionService,
) : BuildingEvaluationService {

	override fun getEvaluation(building: ObjBuilding): BuildingEvaluationResult {
		require(building.currentRating != null) { "has current rating" }

		var value: String? = null

		val projectionResult =
			this.projectionService.getProjection(
				Set.of<ObjBuilding>(building),
				ProjectionService.DefaultDuration,
			)

		val facts: MutableList<EvaluationParameter> = mutableListOf()
		val onePageFacts: MutableList<EvaluationParameter> = mutableListOf()

		if (building.buildingNr != null) {
			value = building.buildingNr
			this.addParameter(facts, "Gebäudenummer", value)
			this.addParameter(onePageFacts, "Gebäudenummer", value)
		}
		val currentRating = building.currentRating!!
		if (currentRating.partCatalog != null) {
			value = currentRating.partCatalog!!.defaultName
			this.addParameter(facts, "Gebäudekategorie", value)
			this.addParameter(onePageFacts, "Gebäudekategorie", value)
		}
		if (building.buildingYear != null && building.buildingYear!! > 0) {
			value = building.buildingYear.toString()
			this.addParameter(facts, "Baujahr", value)
			this.addParameter(onePageFacts, "Baujahr", value)
		}
		if (building.insuredValue != null) {
			value = Formatter.formatMonetaryValue(1000 * building.insuredValue!!.toDouble(), "CHF")
			this.addParameter(facts, "GV-Neuwert (" + building.insuredValueYear + ")", value)
			this.addParameter(onePageFacts, "GV-Neuwert (" + building.insuredValueYear + ")", value)
		}
		if (building.volume != null) {
			value = Formatter.formatNumber(building.volume) + " m³"
			this.addParameter(facts, "Volumen Rauminhalt SIA 416", value)
		}
		if (currentRating.ratingDate != null) {
			value = Formatter.formatDate(currentRating.ratingDate!!)
			this.addParameter(facts, "Begehung am", value)
		}

		val timeValue = Formatter.formatMonetaryValue(projectionResult.periodList.get(0).timeValue, "CHF")
		val shortTermRestoration = Formatter.formatMonetaryValue(this.getRestorationCosts(projectionResult, 0, 1), "CHF")
		val midTermRestoration = Formatter.formatMonetaryValue(this.getRestorationCosts(projectionResult, 2, 5), "CHF")
		val longTermRestoration = Formatter.formatMonetaryValue(this.getRestorationCosts(projectionResult, 6, 25), "CHF")
		val averageMaintenance =
			Formatter.formatMonetaryValue(this.getAverageMaintenanceCosts(projectionResult, 1, 5), "CHF")

		var ratingYear = 9999
		var elementCount = 0
		var totalWeight = 0
		var totalCondition = 0
		val elements: MutableList<EvaluationElement> = mutableListOf()
		for (element in currentRating.elementList) {
			if (element.weight != null && element.weight!! > 0) {
				var description = this.replaceEol(element.description)
				if (!StringUtils.isEmpty(element.conditionDescription)) {
					description += "<br/><b>Zustand</b>: " + element.conditionDescription
				}
				if (!StringUtils.isEmpty(element.measureDescription)) {
					description += "<br/><b>Massnahmen</b>: " + element.measureDescription
				}
				val dto =
					EvaluationElement(
						name = element.buildingPart!!.defaultName,
						description = description,
						weight = element.weight,
						condition = element.condition,
						conditionColor = getConditionColor(element.condition),
						restorationYear =
							this.getRestorationYear(projectionResult, element.buildingPart!!),
						restorationCosts =
							this.getRestorationCosts(projectionResult, element.buildingPart!!),
					)
				elements.add(dto)
				if (element.ratingYear!! < ratingYear) {
					ratingYear = element.ratingYear!!
				}
				elementCount += 1
				totalWeight += element.weight!!
				totalCondition += element.condition!!
			}
		}

		totalCondition = Math.round((totalCondition / elementCount).toFloat())
		val totalDto =
			EvaluationElement(
				name = "Total",
				weight = totalWeight,
				condition = totalCondition,
				conditionColor = getConditionColor(totalCondition),
			)
		elements.add(totalDto)

		val params: MutableList<EvaluationParameter> = mutableListOf()
		this.addParameter(params, "Laufzeit (Zeithorizont)", "25 Jahre")
		this.addParameter(params, "Teuerung", String.format("%.1f", building.inflationRate) + " %")
		this.addParameter(params, "Z/N Wert", "" + totalCondition)
		this.addParameter(params, "Zeitwert", timeValue)
		this.addParameter(params, "IS Kosten kurzfristig (0 - 1 Jahre)", shortTermRestoration)
		this.addParameter(params, "IS Kosten mittelfristig (2 - 5 Jahre)", midTermRestoration)
		this.addParameter(params, "IS Kosten langfristig (6 - 25 Jahre)", longTermRestoration)
		this.addParameter(params, "Durchschnittliche IH Kosten (nächste 5 Jahre)", averageMaintenance)

		val onePageParams: MutableList<EvaluationParameter> = mutableListOf()
		this.addParameter(
			onePageParams,
			"Laufzeit (Zeithorizont); Teuerung",
			"25 Jahre; " + String.format("%.1f", building.inflationRate) + " %",
		)
		this.addParameter(onePageParams, "Zeitwert (Z/N Wert: " + totalCondition + ")", timeValue)
		this.addParameter(onePageParams, "IS Kosten kurzfristig (0 - 1 Jahre)", shortTermRestoration)
		this.addParameter(onePageParams, "IS Kosten mittelfristig (2 - 5 Jahre)", midTermRestoration)
		this.addParameter(onePageParams, "IS Kosten langfristig (6 - 25 Jahre)", longTermRestoration)
		this.addParameter(
			onePageParams,
			"Durchschnittliche IH Kosten (nächste 5 Jahre)",
			averageMaintenance,
		)

		val periods: MutableList<EvaluationPeriod> = mutableListOf()
		var aggrCosts = 0
		for (pp in projectionResult.periodList) {
			val totalCosts = (pp.maintenanceCosts + pp.restorationCosts).toInt()
			aggrCosts += totalCosts
			var restorationElement: String = ""
			if (pp.restorationElements.size == 1) {
				restorationElement = pp.restorationElements[0].buildingPart.name!!
			}
			val eps =
				EvaluationPeriod(
					year = pp.year,
					originalValue = pp.originalValue.toInt(),
					timeValue = pp.timeValue.toInt(),
					maintenanceCosts = pp.maintenanceCosts.toInt(),
					restorationCosts = pp.restorationCosts.toInt(),
					restorationElement = restorationElement,
					restorationBuilding = null,
					totalCosts = totalCosts,
					aggrCosts = aggrCosts,
				)
			periods.add(eps)
			if (pp.restorationElements.size > 1) {
				for (re in pp.restorationElements) {
					restorationElement = re.buildingPart.name!!
					val epd =
						EvaluationPeriod(
							year = null,
							originalValue = null,
							timeValue = null,
							maintenanceCosts = null,
							restorationCosts = re.restorationCosts.toInt(),
							restorationElement = restorationElement,
							restorationBuilding = null,
						)
					periods.add(epd)
				}
			}
		}

		return BuildingEvaluationResult(
			id = building.id as Int,
			name = building.name,
			description = this.replaceEol(building.description),
			address = building.street + ", " + building.zip + " " + building.city + ", " + building.country!!.defaultName,
			accountName = building.account!!.name,
			facts = facts,
			params = params,
			onePageFacts = onePageFacts,
			onePageParams = onePageParams,
			ratingYear = ratingYear,
			elements = elements,
			startYear = projectionResult.startYear,
			periods = periods,
		)
	}

	private fun replaceEol(text: String?): String = if (text != null) text.replace("<br>", SOFT_RETURN) else ""

	private fun getAverageMaintenanceCosts(
		projectionResult: ProjectionResult,
		startYear: Int,
		endYear: Int,
	): Int {
		require(startYear <= endYear) { "valid years" }
		var costs = 0.0
		for (pp in projectionResult.periodList) {
			val yearSinceStart = pp.year - projectionResult.startYear
			if (startYear <= yearSinceStart && yearSinceStart <= endYear) {
				costs += pp.maintenanceCosts
			}
		}
		return NumericUtils.roundProgressive(costs / (endYear - startYear + 1)).toInt()
	}

	private fun getRestorationCosts(
		projectionResult: ProjectionResult,
		startYear: Int,
		endYear: Int,
	): Int {
		require(startYear <= endYear) { "valid years" }
		var costs = 0.0
		for (pp in projectionResult.periodList) {
			val yearSinceStart = pp.year - projectionResult.startYear
			if (startYear <= yearSinceStart && yearSinceStart <= endYear) {
				costs += pp.restorationCosts
			}
		}
		return NumericUtils.roundProgressive(costs).toInt()
	}

	private fun getRestorationYear(
		projectionResult: ProjectionResult,
		buildingPart: CodeBuildingPart,
	): Int? {
		for (pp in projectionResult.periodList) {
			for (re in pp.restorationElements) {
				if (re.buildingPart.id == buildingPart.id) {
					return pp.year
				}
			}
		}
		return null
	}

	private fun getRestorationCosts(
		projectionResult: ProjectionResult,
		buildingPart: CodeBuildingPart,
	): Int? {
		for (pp in projectionResult.periodList) {
			for (re in pp.restorationElements) {
				if (re.buildingPart.id == buildingPart.id) {
					return NumericUtils.roundProgressive(re.restorationCosts).toInt()
				}
			}
		}
		return null
	}

	private fun addParameter(
		list: MutableList<EvaluationParameter>,
		name: String,
		value: String?,
	) {
		if (value != null) {
			list.add(
				EvaluationParameter(
					name = name,
					value = value,
				),
			)
		}
	}

	companion object {

		const val SOFT_RETURN: String = "\u000B"

		@JvmStatic
		val VERY_BAD_CONDITION: Color = Color(229, 79, 41)
		val BAD_CONDITION: Color = Color(250, 167, 36)
		val OK_CONDITION: Color = Color(120, 192, 107)

		@JvmStatic
		val GOOD_CONDITION: Color = Color(51, 135, 33)

		fun getConditionColor(condition: Int?): Color? {
			if (condition == null) {
				return null
			} else if (condition < 50) {
				return VERY_BAD_CONDITION
			} else if (condition < 70) {
				return BAD_CONDITION
			} else if (condition < 85) {
				return OK_CONDITION
			}
			return GOOD_CONDITION
		}

	}

}
