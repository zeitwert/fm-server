package io.zeitwert.fm.building.service.api.dto

import io.zeitwert.fm.util.Formatter
import java.awt.Color

data class BuildingEvaluationResult(
	val id: Int,
	val name: String?,
	val description: String?,
	val address: String?,
	val accountName: String?,
	val facts: List<EvaluationParameter>,
	val params: List<EvaluationParameter>,
	val onePageFacts: List<EvaluationParameter>,
	val onePageParams: List<EvaluationParameter>,
	val ratingYear: Int,
	val elements: List<EvaluationElement>,
	val startYear: Int,
	val periods: List<EvaluationPeriod>,
)

data class EvaluationParameter(
	val name: String?,
	val value: String?,
)

data class EvaluationElement(
	val name: String?,
	val buildingName: String? = null,
	val elementName: String? = null,
	val description: String? = null,
	val weight: Int? = null,
	val condition: Int? = null,
	val conditionColor: Color? = null,
	val restorationYear: Int? = null,
	val restorationCosts: Int? = null,
	val shortTermCosts: Int? = null,
	val midTermCosts: Int? = null,
	val longTermCosts: Int? = null,
) {

	val formattedShortTermCosts: String
		get() = Formatter.INSTANCE.formatNumber(shortTermCosts)

	val formattedMidTermCosts: String
		get() = Formatter.INSTANCE.formatNumber(midTermCosts)

	val formattedLongTermCosts: String
		get() = Formatter.INSTANCE.formatNumber(longTermCosts)

}

data class EvaluationPeriod(
	val year: Int?,
	val originalValue: Int?,
	val timeValue: Int?,
	val maintenanceCosts: Int?,
	val restorationCosts: Int?,
	val restorationElement: String?,
	val restorationBuilding: String?,
	val totalCosts: Int?,
	val aggrCosts: Int?,
)
