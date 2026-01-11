package io.zeitwert.fm.portfolio.api.dto

import io.zeitwert.fm.building.api.dto.EvaluationBuilding
import io.zeitwert.fm.building.api.dto.EvaluationElement
import io.zeitwert.fm.building.api.dto.EvaluationPeriod

data class PortfolioEvaluationResult(
	val id: Int,
	val name: String,
	val description: String,
	val accountName: String,

	val buildings: List<EvaluationBuilding>,

	val elements: List<EvaluationElement>,

	val startYear: Int,
	val periods: List<EvaluationPeriod>,
)
