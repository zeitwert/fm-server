package io.zeitwert.fm.building.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeBuildingType(
	override val defaultName: String,
) : Enumerated {

	T01("01 Wohngebäude"),
	T02("02 Schulen"),
	T03("03 Industriebauten"),
	T04("04 Landw. Gebäude"),
	T05("05 Techn. Betriebe"),
	T06("06 Handel und Verwaltung"),
	T07("07 Justiz und Polizei"),
	T08("08 Fürsorge und Gesundheit"),
	T09("09 Kultus"),
	T10("10 Kultur und Geselligkeit"),
	T11("11 Gastgewerbe"),
	T12("12 Freizeit, Sport, Erholung"),
	T13("13 Verkehrsanlagen"),
	T14("14 Militär- und Schutzanlagen"),
	T15("15 Schutzbauten"),
	;

	override val enumeration get() = Enumeration

	override val id = name

	companion object Enumeration : EnumerationBase<CodeBuildingType>(CodeBuildingType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getBuildingType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
