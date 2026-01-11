package io.zeitwert.fm.building.api.dto

import io.zeitwert.fm.util.Formatter
import java.awt.Color
import kotlin.math.roundToInt

data class EvaluationBuilding(
	val id: Int,
	val name: String?,
	val description: String?,
	val buildingNr: String?,
	val address: String?,
	val insuredValue: Int?,
	val relativeValue: Int,
	val insuredValueYear: Int,
	val ratingYear: Int,
	val condition: Int,
	val conditionColor: Color,
) : Comparable<EvaluationBuilding> {

	val formattedInsuredValue: String
		get() = Formatter.formatNumber(1000 * (insuredValue ?: 0))

	fun getRelativeValue(): String {
		val weight = (relativeValue * 70.0 / 100).roundToInt()
		return String(CharArray(weight)).replace('\u0000', 'I')
	}

	override fun compareTo(other: EvaluationBuilding): Int {
		var res = buildingNr!!.compareTo(other.buildingNr!!)
		if (res == 0) {
			res = name!!.compareTo(other.name!!)
		}
		return res
	}

}
